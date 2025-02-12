package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.lifecycle.ViewModel
import com.the_stilton_assistants.dealdetective.model.Product
import com.the_stilton_assistants.dealdetective.model.ProductsOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AProductsViewModel: ViewModel() {
    protected val _productsOrderMutableState: MutableStateFlow<ProductsOrder> =
        MutableStateFlow(ProductsOrder.ASC)
    val productsOrderState: StateFlow<ProductsOrder> = _productsOrderMutableState.asStateFlow()

    fun setProductsOrder(order: ProductsOrder) {
        _productsOrderMutableState.value = order
    }

    protected val _searchQueryMutableState: MutableStateFlow<String> = MutableStateFlow("")
    val searchQueryState: StateFlow<String> = _searchQueryMutableState.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQueryMutableState.value = query
    }


    protected open fun reordersProducts(products: List<Product>): List<Product> {
        return if (_productsOrderMutableState.value == ProductsOrder.ASC) {
            products.sortedBy { it.productName }
        } else {
            products.sortedByDescending { it.productName }
        }
    }

    protected open fun applyFilter(products: List<Product>): List<Product> {
        return products.filter { product ->
            val searchQuery = _searchQueryMutableState.value
            val matchesSearchQuery = searchQuery.isEmpty() || product.productName.contains(searchQuery, ignoreCase = true)
            matchesSearchQuery
        }
    }
}
