package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.service.StoresServiceHandler
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * ViewModel to retrieve all items in the Room database.
 */
class ProductsViewModel(
    private val productRepository: IProductsRepository,
    private val storesServiceHandler: StoresServiceHandler,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : AProductsViewModel() {
    /**
     * Holds deals ui state. The list of items are retrieved from [IProductsRepository] and mapped
     * to [CategoriesUiState]
     */
    private val _categoriesUiMutableState: MutableStateFlow<CategoriesUiState> =
        MutableStateFlow(CategoriesUiState.Loading)
    val categoriesUiState: StateFlow<CategoriesUiState> = _categoriesUiMutableState.asStateFlow()

    private val _storeSelectedMutableState: MutableStateFlow<StoreId> =
        MutableStateFlow(StoreId.UNKNOWN)
    val storeSelectedState: StateFlow<StoreId> = _storeSelectedMutableState.asStateFlow()

    private val _categoriesMutableState = MutableStateFlow(mutableListOf<String>())
    val categoriesState: StateFlow<List<String>> = _categoriesMutableState.asStateFlow()

    private var subScope: CoroutineScope? = null
    private var isInitialized = false

    @OptIn(ExperimentalCoroutinesApi::class)
    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            _storeSelectedMutableState.collect { storeId ->
                _categoriesMutableState.value.clear()
                val flowResult = if (storeId == StoreId.UNKNOWN) {
                    productRepository.getAllProductsFlow()
                } else {
                    productRepository.getProductsByStoreFlow(storeId.value)
                }
                if (flowResult is Result.Error) {
                    _categoriesUiMutableState.value =
                        CategoriesUiState.Error(flowResult.error.message)
                    return@collect
                }

                _categoriesUiMutableState.value = CategoriesUiState.Loading

                runBlocking {
                    if (subScope != null) {
                        subScope!!.coroutineContext.cancelChildren()
                    }
                    subScope = CoroutineScope(viewModelScope.coroutineContext + Job())
                }
                subScope?.launch {
                    val flow = (flowResult as Result.Success).data
                    flow.flatMapLatest { products ->
                        combine(
                            _categoriesMutableState,
                            _searchQueryMutableState,
                            _productsOrderMutableState
                        ) { _, _, _ ->
                            withContext(dispatcherDefault) {
                                if (products.isEmpty()) {
                                    return@withContext products to products
                                }
                                val filteredProducts = applyFilter(products)
                                val reorderedProducts = reordersProducts(filteredProducts)
                                products to reorderedProducts
                            }
                        }
                    }.collect { (products, reorderedProducts) ->
                        _categoriesUiMutableState.update { currentState ->
                            if (currentState is CategoriesUiState.Loading) {
                                CategoriesUiState.Display(
                                    filteredProductList = reorderedProducts,
                                    productList = products,
                                )
                            } else {
                                (currentState as CategoriesUiState.Display).copy(
                                    filteredProductList = reorderedProducts,
                                    productList = products,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun getCategoryList(storeId: StoreId): List<Pair<String, String>> {
        require(storeId != StoreId.UNKNOWN)
        return storesServiceHandler.getStoreCategory(storeId)
    }

    fun selectStore(storeId: StoreId) {
        _storeSelectedMutableState.value = storeId
    }

    fun addCategory(category: String) {
        require(!_categoriesMutableState.value.contains(category))
        _categoriesMutableState.update { currentList ->
            currentList.toMutableList().apply { add(category) }
        }
    }

    fun removeCategory(category: String) {
        require(_categoriesMutableState.value.contains(category))
        _categoriesMutableState.update { currentList ->
            currentList.toMutableList().apply { remove(category) }
        }
    }

    override fun applyFilter(products: List<Product>): List<Product> {
        return products.filter { product ->
            val matchesCategory =
                _categoriesMutableState.value.isEmpty() || _categoriesMutableState.value.contains(
                    product.category
                )
            val searchQuery = _searchQueryMutableState.value
            val matchesSearchQuery = searchQuery.isEmpty() || product.productName.contains(
                searchQuery,
                ignoreCase = true
            )
            matchesCategory && matchesSearchQuery
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val productRepository = dealDetectiveAppContainer().productsRepository
                val storesServiceHandler = dealDetectiveAppContainer().storesServiceHandler
                ProductsViewModel(
                    productRepository = productRepository,
                    storesServiceHandler = storesServiceHandler,
                )
            }
        }
    }
}

/**
 * Ui State for Categories screen.
 */
sealed interface CategoriesUiState {
    object Loading : CategoriesUiState
    data class Display(
        val filteredProductList: List<Product> = listOf(),
        val productList: List<Product> = listOf(),
    ) : CategoriesUiState
    data class Error(val message: String) : CategoriesUiState
}
