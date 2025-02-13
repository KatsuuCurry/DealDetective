package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.lifecycle.ViewModel
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.ProductsOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base class for view models that handle products.
 */
abstract class AProductsViewModel: ViewModel() {
    /**
     * Mutable state for the order of the products.
     */
    protected val _productsOrderMutableState: MutableStateFlow<ProductsOrder> =
        MutableStateFlow(ProductsOrder.ASC)
    val productsOrderState: StateFlow<ProductsOrder> = _productsOrderMutableState.asStateFlow()

    /**
     * Sets the order of the products.
     */
    fun setProductsOrder(order: ProductsOrder) {
        _productsOrderMutableState.value = order
    }

    /**
     * Mutable state for the search query.
     */
    protected val _searchQueryMutableState: MutableStateFlow<String> = MutableStateFlow("")
    val searchQueryState: StateFlow<String> = _searchQueryMutableState.asStateFlow()

    /**
     * Sets the search query.
     */
    fun setSearchQuery(query: String) {
        _searchQueryMutableState.value = query
    }

    /**
     * Filters and reorders the products.
     */
    protected open fun reordersProducts(products: List<Product>): List<Product> {
        return if (_productsOrderMutableState.value == ProductsOrder.ASC) {
            products.sortedBy { it.productName }
        } else {
            products.sortedByDescending { it.productName }
        }
    }

    /**
     * Filters the products based on the search query.
     */
    protected open fun applyFilter(products: List<Product>): List<Product> {
        return products.filter { product ->
            val searchQuery = _searchQueryMutableState.value
            val matchesSearchQuery = searchQuery.isEmpty() || product.productName.contains(searchQuery, ignoreCase = true)
            matchesSearchQuery
        }
    }
}
