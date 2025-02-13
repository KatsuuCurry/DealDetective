package com.the_stilton_assistants.dealdetective.viewmodel

import androidx.lifecycle.ViewModel
import com.the_stilton_assistants.dealdetective.util.INotificationBubbleHandler.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Base class for view models that handle operations.
 */
abstract class BaseViewModel() : ViewModel() {
    /**
     * Mutable state for the operation UI state.
     */
    protected var operationUiMutableState: MutableStateFlow<OperationUiState> =
        MutableStateFlow(OperationUiState.Idle)
    /**
     * State for the operation UI state.
     */
    val operationUiState: StateFlow<OperationUiState> = operationUiMutableState.asStateFlow()

    /**
     * Starts an operation.
     */
    protected fun startOperation() {
        require(operationUiMutableState.value != OperationUiState.Loading) {
            "Another operation is in progress"
        }
        operationUiMutableState.value = OperationUiState.Loading
    }

    /**
     * Resets the operation.
     */
    fun resetOperation() {
        operationUiMutableState.value = OperationUiState.Idle
    }
}

/**
 * UI state for operations.
 */
sealed interface OperationUiState {
    object Idle : OperationUiState
    object Loading : OperationUiState
    data class Success(
        val message: String,
        val notificationType: NotificationType = NotificationType.INFO
    ) : OperationUiState
    data class Error(val message: String) : OperationUiState
}

/**
 * Success messages for operations.
 */
sealed interface SuccessMessage {
    val message: String
}

/**
 * Error messages for operations.
 */
sealed interface ErrorMessage {
    val message: String
}
