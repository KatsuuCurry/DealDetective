package com.the_stilton_assistants.dealdetective.util

import android.widget.Toast
import androidx.activity.ComponentActivity
import com.the_stilton_assistants.dealdetective.model.NotificationFilter

class NotificationBubbleHandler(
    private val context: ComponentActivity,
) : INotificationBubbleHandler {

    override var notificationFilter: NotificationFilter = NotificationFilter.ALL

    override fun displayBubble(message: String, type: NotificationType) {
        if ((notificationFilter == NotificationFilter.IMPORTANT && type !in listOf(
                NotificationType.IMPORTANT,
                NotificationType.ERROR,
                NotificationType.FORCE
            )) || (notificationFilter == NotificationFilter.ERROR_ONLY && type !in listOf(
                NotificationType.ERROR,
                NotificationType.FORCE
            ))
        ) {
            return
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
