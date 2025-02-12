package com.the_stilton_assistants.dealdetective.util

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.the_stilton_assistants.dealdetective.util.IImagePicker.Companion.ImagePickerError
import com.the_stilton_assistants.dealdetective.util.IImagePicker.Companion.MAX_FILE_SIZE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImagePicker(private val activity: ComponentActivity) : IImagePicker {
    private val contract = ActivityResultContracts.GetContent()
    private lateinit var onImagePickedCallback: (Uri?, ImagePickerError) -> Unit
    private lateinit var resultLauncher: ActivityResultLauncher<String>

    override fun registerAction(
        onImagePicked: (Uri?, ImagePickerError) -> Unit
    ) {
        onImagePickedCallback = onImagePicked
        resultLauncher = activity.activityResultRegistry.register("IMG", contract) { result ->
            Log.d("ImagePickerImpl", "Image picked: $result")
            if (result == null) {
                onImagePickedCallback(null, ImagePickerError.NoError)
                return@register
            }
            try {
                var imageUri: Uri? = null
                activity.contentResolver.openInputStream(result).use {
                    val byteBuffer = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    var len: Int
                    var totalSize = 0
                    while (it?.read(buffer).also { len = it ?: -1 } != -1) {
                        totalSize += len
                        if (totalSize > MAX_FILE_SIZE) {
                            onImagePickedCallback(null, ImagePickerError.FileTooLarge)
                            return@register
                        }
                        byteBuffer.write(buffer, 0, len)
                    }

                    val file = File(activity.filesDir, "UserImage")
                    FileOutputStream(file).use { output ->
                        output.write(byteBuffer.toByteArray())
                    }
                    imageUri = Uri.fromFile(file)
                }
                onImagePickedCallback(imageUri, ImagePickerError.NoError)
            } catch (e: Exception) {
                Log.e("ImagePickerImpl", "Error reading image", e)
                onImagePickedCallback(null, ImagePickerError.Unknown)
            }
        }
    }

    override fun unregisterAction() {
        resultLauncher.unregister()
    }

    override fun pickImage() {
        resultLauncher.launch("image/*")
    }

    override fun clearImage() {
        val file = File(activity.filesDir, "UserImage")
        if (file.exists()) {
            file.delete()
        }
    }
}
