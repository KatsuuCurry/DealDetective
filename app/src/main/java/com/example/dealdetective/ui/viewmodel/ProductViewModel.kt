package com.example.dealdetective.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.dealdetective.ui.ProductsOrder

abstract class ProductViewModel: ViewModel() {
    var productsOrder: ProductsOrder = ProductsOrder.ASC
        set(value) {
            field = value
            updateState()
        }
    var searchQuery: String = ""
        set(value) {
            field = value
            updateState()
        }

    abstract fun updateState()
}