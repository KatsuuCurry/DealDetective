package com.the_stilton_assistants.dealdetective

import androidx.activity.ComponentActivity
import com.the_stilton_assistants.dealdetective.util.IImagePicker
import com.the_stilton_assistants.dealdetective.util.INotificationBubbleHandler
import com.the_stilton_assistants.dealdetective.util.ImagePicker
import com.the_stilton_assistants.dealdetective.util.NotificationBubbleHandler

/**
 * App container for Dependency injection.
 */
interface IActivityContainer {
    /**
     * Handler for displaying messages to the user
     */
    val notificationBubbleHandler: INotificationBubbleHandler

    /**
     * Image picker
     */
    val imagePicker: IImagePicker
}

class ActivityContainer(private val activity: ComponentActivity) : IActivityContainer {

    /**
     * Implementation of [INotificationBubbleHandler]
     */
    override val notificationBubbleHandler: INotificationBubbleHandler by lazy {
        NotificationBubbleHandler(activity)
    }

    /**
     * Implementation of [IImagePicker]
     */
    override val imagePicker: IImagePicker by lazy {
        ImagePicker(activity)
    }
}
