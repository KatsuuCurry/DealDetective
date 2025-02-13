package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.util.Result
import com.the_stilton_assistants.dealdetective.viewmodel.ProductErrorMessages.*
import com.the_stilton_assistants.dealdetective.viewmodel.ProductSuccessMessages.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve a Product.
 */
class ProductViewModel(
    private val productRepository: IProductsRepository,
) : BaseViewModel() {

    /**
     * Holds product ui state. The list of items are retrieved from [IProductsRepository]
     * and mapped to [ProductUiState]
     */
    private val _productUiMutableState: MutableStateFlow<ProductUiState> =
        MutableStateFlow(ProductUiState.Loading)
    val productUiState: StateFlow<ProductUiState> = _productUiMutableState.asStateFlow()

    var isInitialized = false

    /**
     * Initializes the view model.
     */
    @MainThread
    fun initialize(storeId: Int, productName: String) {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            val flowResult = productRepository.getProductByIdFlow(storeId, productName)
            if (flowResult is Result.Error) {
                _productUiMutableState.value = ProductUiState.Error(flowResult.error.message)
                return@launch
            }
            val flow = (flowResult as Result.Success).data

            flow.collect { product ->
                _productUiMutableState.update { currentState ->
                    if (product == null) {
                        ProductUiState.Error(ProductNotFound.message)
                    } else if (currentState is ProductUiState.Loading) {
                        ProductUiState.Display(product)
                    } else {
                        (currentState as ProductUiState.Display).copy(
                            product = product,
                        )
                    }
                }
            }
        }
    }

    /**
     * Updates the favorite status of the product.
     */
    fun updateFavoriteStatus(isFavorite: Boolean) {
        startOperation()
        viewModelScope.launch {
            if (_productUiMutableState.value !is ProductUiState.Display) {
                operationUiMutableState.value = OperationUiState.Error(
                    ProductNotFound.message
                )
                return@launch
            }
            productRepository.updateFavoriteStatus(
                (_productUiMutableState.value as ProductUiState.Display).product,
                isFavorite
            ).onError {
                    operationUiMutableState.value = OperationUiState.Error(it.message)
                }
                .onSuccess {
                    operationUiMutableState.value = OperationUiState.Success(
                        FavoriteStatusUpdated.message
                    )
                }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                ProductViewModel(dealDetectiveAppContainer().productsRepository)
            }
        }
    }
}

/**
 * Ui State for Product Screen.
 */
sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Display(val product: Product) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

/**
 * Success messages for the ProductViewModel.
 */
sealed interface ProductSuccessMessages : SuccessMessage {
    object FavoriteStatusUpdated : ProductSuccessMessages {
        override val message = "Prodotto Aggiunto alla Lista della Spesa"
    }
}

/**
 * Error messages for the ProductViewModel.
 */
sealed interface ProductErrorMessages : ErrorMessage {
    object ProductNotFound : ProductErrorMessages {
        override val message = "Prodotto non trovato"
    }
}
