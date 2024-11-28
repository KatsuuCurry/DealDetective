package com.example.dealdetective.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dealdetective.storage.room.Product
import com.example.dealdetective.storage.room.ProductRepository
import com.example.dealdetective.ui.ProductsOrder
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
class CategoriesViewModel(private val productRepository: ProductRepository) : ProductViewModel() {

    private val dispatcherIo = Dispatchers.IO
    private val dispatcherDefault = Dispatchers.Default

    /**
     * Holds deals ui state. The list of items are retrieved from [ProductRepository] and mapped to
     * [CategoriesUiState]
     */
    private val _categoriesUiState: MutableStateFlow<CategoriesUiState> = MutableStateFlow(CategoriesUiState())
    val categoriesUiState: StateFlow<CategoriesUiState> = _categoriesUiState.asStateFlow()

    private val categories = mutableListOf<String>()
    private val disabledCategories = mutableListOf<String>()
    val categoriesList: List<String>
        get() = categories + disabledCategories

    init {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {
            productRepository.getAllProductsFlow().collect { it ->
                _categoriesUiState.update { currentState ->
                    currentState.copy(productList = it)
                    currentState.copy(filteredProductList = reordersProducts(applyFilter(it)))
                }
            }
        }
    }

    override fun updateState() {
        viewModelScope.launch(dispatcherDefault) {
            _categoriesUiState.update { currentState ->
                currentState.copy(filteredProductList = reordersProducts(applyFilter(currentState.productList)))
            }
        }
    }

    fun addCategory(category: String) {
        categories.add(category)
        updateState()
    }

    fun removeCategory(category: String) {
        categories.remove(category)
        updateState()
    }

    fun toggleCategory(category: String) {
        if (categories.contains(category)) {
            categories.remove(category)
            disabledCategories.add(category)
        } else if (disabledCategories.contains(category)) {
            disabledCategories.remove(category)
            categories.add(category)
        } else {
            throw IllegalStateException("Category not found")
        }
        updateState()
    }

    private fun reordersProducts(products: List<Product>): List<Product> {
        return if (productsOrder == ProductsOrder.ASC) {
            products.sortedBy { it.productName }
        } else {
            products.sortedByDescending { it.productName }
        }
    }

    private fun applyFilter(products: List<Product>): List<Product> {
        return products.filter { product ->
            val matchesCategory = categories.isEmpty() || categories.contains(product.category)
            val matchesSearchQuery = searchQuery.isEmpty() || product.productName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearchQuery
        }
    }
}

/**
 * Ui State for Categories screen.
 */
data class CategoriesUiState(val filteredProductList: List<Product> = listOf(), val productList: List<Product> = listOf())