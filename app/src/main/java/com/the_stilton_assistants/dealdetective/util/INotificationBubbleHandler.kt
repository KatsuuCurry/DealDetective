package com.the_stilton_assistants.dealdetective.util

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.the_stilton_assistants.dealdetective.MainActivity
import com.the_stilton_assistants.dealdetective.model.NotificationFilter

/**
 * Interface for displaying messages to the user
 */
interface INotificationBubbleHandler {
    var notificationFilter: NotificationFilter

    /**
     * Show a message to the user
     *
     * @param message The message to be shown
     */
    fun displayBubble(message: String, type: NotificationType = NotificationType.INFO)
}

enum class NotificationType {
    INFO,
    IMPORTANT,
    ERROR,
    FORCE
}

/**
 * Remember the notification handler
 */
@Composable
fun rememberNotificationBubbleHandler() : INotificationBubbleHandler {
    val handler =
        (LocalActivity.current as MainActivity).activityContainer.notificationBubbleHandler
    return remember { handler }
}
