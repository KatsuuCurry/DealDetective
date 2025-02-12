package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.lifecycle.ViewModel
import com.the_stilton_assistants.dealdetective.util.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel() : ViewModel() {
    protected var operationUiMutableState: MutableStateFlow<OperationUiState> =
        MutableStateFlow(OperationUiState.Idle)
    val operationUiState: StateFlow<OperationUiState> = operationUiMutableState.asStateFlow()

    protected fun startOperation() {
        require(operationUiMutableState.value != OperationUiState.Loading) {
            "Another operation is in progress"
        }
        operationUiMutableState.value = OperationUiState.Loading
    }

    fun resetOperation() {
        operationUiMutableState.value = OperationUiState.Idle
    }
}

sealed interface OperationUiState {
    object Idle : OperationUiState
    object Loading : OperationUiState
    data class Success(
        val message: String,
        val notificationType: NotificationType = NotificationType.INFO
    ) : OperationUiState
    data class Error(val message: String) : OperationUiState
}

sealed interface SuccessMessage {
    val message: String
}

sealed interface ErrorMessage {
    val message: String
}
