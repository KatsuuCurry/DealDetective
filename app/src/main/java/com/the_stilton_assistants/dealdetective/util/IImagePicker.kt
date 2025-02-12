package com.the_stilton_assistants.dealdetective.util

import android.net.Uri
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LifecycleStartEffect
import com.the_stilton_assistants.dealdetective.MainActivity
import com.the_stilton_assistants.dealdetective.util.IImagePicker.Companion.ImagePickerError

/**
 * Interface for picking images from the device
 */
interface IImagePicker {
    /**
     * Register a callback to be called when an image is picked
     *
     * @param onImagePicked The callback to be called when an image is picked
     */
    fun registerAction(onImagePicked: (Uri?, ImagePickerError) -> Unit)

    /**
     * Unregister the callback
     */
    fun unregisterAction()

    /**
     * Pick an image from the device
     */
    fun pickImage()

    /**
     * Clear the image
     */
    fun clearImage()

    companion object {
        const val MAX_FILE_SIZE: Int = 8 * 1024 * 1024 // 8MB

        enum class ImagePickerError() {
            NoError,
            PermissionDenied,
            FileTooLarge,
            Unknown,
        }
    }
}

/**
 * Remember the image picker
 *
 * @param callback The callback to be called when an image is picked
 */
@Composable
fun rememberImagePicker(callback: (Uri?, ImagePickerError) -> Unit) : IImagePicker {
    val imagePicker = (LocalActivity.current!! as MainActivity).activityContainer.imagePicker
    LifecycleStartEffect(key1 = Unit) {
        imagePicker.registerAction(callback)

        onStopOrDispose {
            imagePicker.unregisterAction()
        }
    }
    return remember { imagePicker }
}
