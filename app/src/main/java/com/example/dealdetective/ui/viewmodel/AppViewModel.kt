package com.example.dealdetective.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dealdetective.AppContainer
import com.example.dealdetective.util.WifiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class AppViewModel(private val appContainer: AppContainer) : ViewModel() {

    private val dispatcherIo = Dispatchers.Default

    /**
     * Holds deals wifi state.
     */
    private val _wifiAppState: MutableStateFlow<WifiAppState> = MutableStateFlow(WifiAppState())
    val wifiAppState: StateFlow<WifiAppState> = _wifiAppState.asStateFlow()

    init {
        updateState()
    }

    private fun updateState() {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(dispatcherIo) {

        }
    }
}

/**
 * Ui State for Categories screen.
 */
data class WifiAppState(val status: WifiStatus.Status = WifiStatus.Status.Unavailable)

data class TopBarAppState(val title: String = "Deals Detective")