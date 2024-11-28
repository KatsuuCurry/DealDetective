package com.example.dealdetective.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dealdetective.storage.room.Product
import com.example.dealdetective.storage.room.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class DealsViewModel(private val productRepository: ProductRepository) : ProductViewModel() {

    /**
     * Holds deals ui state. The list of items are retrieved from [ProductRepository] and mapped to
     * [DealsUiState]
     */
    private val dispatcherIo = Dispatchers.IO
    private val _dealsUiState: MutableStateFlow<DealsUiState> = MutableStateFlow(DealsUiState())
    val dealsUiState: StateFlow<DealsUiState> = _dealsUiState.asStateFlow()

    init {
        updateState()
    }

    override fun updateState() {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {
            productRepository.getAllProductsFlow().collect {
                _dealsUiState.update { currentState ->
                    currentState.copy(productList = it)
                }
            }
        }
    }
}

/**
 * Ui State for Deals screen.
 */
data class DealsUiState(val productList: List<Product> = listOf())