package com.the_stilton_assistants.dealdetective.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.the_stilton_assistants.dealdetective.util.INotificationBubbleHandler
import com.the_stilton_assistants.dealdetective.util.NotificationType
import com.the_stilton_assistants.dealdetective.util.rememberNotificationBubbleHandler
import com.the_stilton_assistants.dealdetective.viewmodel.BaseViewModel
import com.the_stilton_assistants.dealdetective.viewmodel.OperationUiState

@Composable
fun handleOperationState(
    viewModel: BaseViewModel,
    notificationBubbleHandler: INotificationBubbleHandler = rememberNotificationBubbleHandler(),
    onSuccess: () -> Unit = { viewModel.resetOperation() },
    onError: () -> Unit = { viewModel.resetOperation() },
): Boolean {
    val operationUiState by viewModel.operationUiState.collectAsStateWithLifecycle()
    val enabled = operationUiState is OperationUiState.Idle

    if (operationUiState is OperationUiState.Success) {
        val message = (operationUiState as OperationUiState.Success).message
        val notificationType = (operationUiState as OperationUiState.Success).notificationType
        notificationBubbleHandler.displayBubble(message, notificationType)
        onSuccess()
    } else if (operationUiState is OperationUiState.Error) {
        val message = (operationUiState as OperationUiState.Error).message
        notificationBubbleHandler.displayBubble(message, NotificationType.ERROR)
        onError()
    }

    return enabled
}
