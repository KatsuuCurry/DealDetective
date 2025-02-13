package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel to retrieve all favourite Products.
 */
class ShoppingListViewModel(
    private val productRepository: IProductsRepository,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : AProductsViewModel() {

    /**
     * Holds shopping list ui state. The list of items are retrieved from [IProductsRepository]
     * and mapped to [ShoppingListUiState]
     */
    private val _shoppingListUiMutableState: MutableStateFlow<ShoppingListUiState> =
        MutableStateFlow(ShoppingListUiState.Loading)
    val shoppingListUiState: StateFlow<ShoppingListUiState> =
        _shoppingListUiMutableState.asStateFlow()

    var isInitialized = false

    /**
     * Initializes the view model.
     */
    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            val flowResult = productRepository.getFavoriteProductsFlow()
            if (flowResult is Result.Error) {
                _shoppingListUiMutableState.value = ShoppingListUiState.Error(
                    flowResult.error.message
                )
                return@launch
            }
            val flow = (flowResult as Result.Success).data

            combine(
                flow,
                _searchQueryMutableState,
                _productsOrderMutableState
            ) { products, _, _ ->
                withContext(dispatcherDefault) {
                    if (products.isEmpty()) {
                        return@withContext products to products
                    }
                    val filteredProducts = applyFilter(products)
                    val reorderedProducts = reordersProducts(filteredProducts)
                    products to reorderedProducts
                }
            }.collect { (products, reorderedProducts) ->
                _shoppingListUiMutableState.update { currentState ->
                    if (currentState is ShoppingListUiState.Loading) {
                        ShoppingListUiState.Display(
                            products,
                            reorderedProducts,
                            totalAmount = calculateTotalAmount(reorderedProducts),
                        )
                    } else {
                        (currentState as ShoppingListUiState.Display).copy(
                            productList = products,
                            filteredProductList = reorderedProducts,
                            totalAmount = calculateTotalAmount(reorderedProducts),
                        )
                    }
                }
            }
        }
    }

    /**
     * Calculates the total amount of the products.
     */
    private suspend fun calculateTotalAmount(products: List<Product>): Double {
        return withContext(dispatcherDefault) {
            products.sumOf { it.discountedPrice }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                ShoppingListViewModel(dealDetectiveAppContainer().productsRepository)
            }
        }
    }
}

/**
 * Ui State for Shopping List Screen.
 */
sealed interface ShoppingListUiState {
    object Loading : ShoppingListUiState
    data class Display(
        val productList: List<Product>,
        val filteredProductList: List<Product>,
        val totalAmount: Double = 0.0
    ) : ShoppingListUiState
    data class Error(val message: String) : ShoppingListUiState
}
