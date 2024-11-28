package com.example.dealdetective.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dealdetective.DealDetectiveApplication

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DealsViewModel(dealDetectiveApplication().container.productsRepository)
        }

        initializer {
            CategoriesViewModel(dealDetectiveApplication().container.productsRepository)
        }

        initializer {
            AppViewModel(dealDetectiveApplication().container)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [DealDetectiveApplication].
 */
fun CreationExtras.dealDetectiveApplication(): DealDetectiveApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DealDetectiveApplication)