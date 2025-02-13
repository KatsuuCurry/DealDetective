package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.ProductsOrder
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
 * ViewModel to retrieve the best deals.
 */
class DealsViewModel(
    private val productRepository: IProductsRepository,
    private val dispatcherDefault: CoroutineDispatcher = Dispatchers.Default,
) : AProductsViewModel() {

    /**
     * Holds deals ui state. The list of items are retrieved from [IProductsRepository] and mapped
     * to [DealsUiState]
     */
    private val _dealsUiMutableState: MutableStateFlow<DealsUiState> =
        MutableStateFlow(DealsUiState.Loading)
    val dealsUiState: StateFlow<DealsUiState> = _dealsUiMutableState.asStateFlow()

    var isInitialized = false

    /**
     * Initializes the view model.
     */
    @MainThread
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            val flowResult = productRepository.getBestDiscountProducts()
            if (flowResult is Result.Error) {
                _dealsUiMutableState.value = DealsUiState.Error(flowResult.error.message)
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
                _dealsUiMutableState.update { currentState ->
                    if (currentState is DealsUiState.Loading) {
                        DealsUiState.Display(products, reorderedProducts)
                    } else {
                        (currentState as DealsUiState.Display).copy(
                            productList = products,
                            filteredProductList = reorderedProducts
                        )
                    }
                }
            }
        }
    }

    /**
     * Reorders the products based on the discount percentage and the product name.
     */
    override fun reordersProducts(products: List<Product>): List<Product> {
        return if (_productsOrderMutableState.value == ProductsOrder.ASC) {
            products.sortedWith(compareByDescending<Product> { calculateDiscountPercentage(it) }
                .thenBy { it.productName })
        } else {
            products.sortedWith(compareByDescending<Product> { calculateDiscountPercentage(it) }
                .thenByDescending { it.productName })
        }
    }

    /**
     * Calculates the discount percentage of a product.
     */
    private fun calculateDiscountPercentage(product: Product): Double {
        return ((product.originalPrice!! - product.discountedPrice) / product.originalPrice!!) * 100
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                DealsViewModel(dealDetectiveAppContainer().productsRepository)
            }
        }
    }
}

/**
 * Ui State for Deals screen.
 */
sealed interface DealsUiState {
    object Loading : DealsUiState
    data class Display(val productList: List<Product>, val filteredProductList: List<Product>) : DealsUiState
    data class Error(val message: String) : DealsUiState
}
