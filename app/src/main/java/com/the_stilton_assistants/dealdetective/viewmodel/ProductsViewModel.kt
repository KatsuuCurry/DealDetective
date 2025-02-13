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
 * ViewModel to retrieve all Products.
 */
class ProductsViewModel(
    private val productRepository: IProductsRepository,
    private val storesServiceHandler: StoresServiceHandler,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : AProductsViewModel() {
    /**
     * Holds Products ui state. The list of items are retrieved from [IProductsRepository] and mapped
     * to [ProductsUiState]
     */
    private val _productsUiMutableState: MutableStateFlow<ProductsUiState> =
        MutableStateFlow(ProductsUiState.Loading)
    val productsUiState: StateFlow<ProductsUiState> = _productsUiMutableState.asStateFlow()

    /**
     * State for the selected store.
     */
    private val _storeSelectedMutableState: MutableStateFlow<StoreId> =
        MutableStateFlow(StoreId.UNKNOWN)
    val storeSelectedState: StateFlow<StoreId> = _storeSelectedMutableState.asStateFlow()

    /**
     * State for the categories.
     */
    private val _categoriesMutableState = MutableStateFlow(mutableListOf<String>())
    val categoriesState: StateFlow<List<String>> = _categoriesMutableState.asStateFlow()

    private var subScope: CoroutineScope? = null
    private var isInitialized = false

    /**
     * Initializes the view model.
     */
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
                    _productsUiMutableState.value =
                        ProductsUiState.Error(flowResult.error.message)
                    return@collect
                }

                _productsUiMutableState.value = ProductsUiState.Loading

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
                        _productsUiMutableState.update { currentState ->
                            if (currentState is ProductsUiState.Loading) {
                                ProductsUiState.Display(
                                    filteredProductList = reorderedProducts,
                                    productList = products,
                                )
                            } else {
                                (currentState as ProductsUiState.Display).copy(
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

    /**
     * Retrieves the list of categories for the selected store.
     */
    fun getCategoryList(storeId: StoreId): List<Pair<String, String>> {
        require(storeId != StoreId.UNKNOWN)
        return storesServiceHandler.getStoreCategory(storeId)
    }

    /**
     * Selects a store.
     */
    fun selectStore(storeId: StoreId) {
        _storeSelectedMutableState.value = storeId
    }

    /**
     * Adds a category to the list of categories.
     */
    fun addCategory(category: String) {
        require(!_categoriesMutableState.value.contains(category))
        _categoriesMutableState.update { currentList ->
            currentList.toMutableList().apply { add(category) }
        }
    }

    /**
     * Removes a category from the list of categories.
     */
    fun removeCategory(category: String) {
        require(_categoriesMutableState.value.contains(category))
        _categoriesMutableState.update { currentList ->
            currentList.toMutableList().apply { remove(category) }
        }
    }

    /**
     * Clears the list of categories.
     */
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
 * Ui State for the Products screen.
 */
sealed interface ProductsUiState {
    object Loading : ProductsUiState
    data class Display(
        val filteredProductList: List<Product> = listOf(),
        val productList: List<Product> = listOf(),
    ) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}
