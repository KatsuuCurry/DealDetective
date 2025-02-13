package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.the_stilton_assistants.dealdetective.DealDetectiveApplication
import com.the_stilton_assistants.dealdetective.IAppContainer
import kotlinx.coroutines.CoroutineScope

/**
 * Extension function to queries for [DealDetectiveApplication] object and returns an instance of
 * [IAppContainer].
 */
fun CreationExtras.dealDetectiveAppContainer(): IAppContainer =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DealDetectiveApplication).appContainer

/**
 * Extension function to queries for [DealDetectiveApplication] object and returns an instance of
 * [CoroutineScope], the app scope.
 */
fun CreationExtras.dealDetectiveAppCoroutineScope(): CoroutineScope =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DealDetectiveApplication).applicationScope
