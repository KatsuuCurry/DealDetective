package com.the_stilton_assistants.dealdetective.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.the_stilton_assistants.dealdetective.DealDetectiveApplication

private const val TAG = "ProductsRetrieverWorker"

class ProductsRetrieverWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        try {
            Log.d(TAG, "Worker started")
            val appContainer = (appContext.applicationContext as DealDetectiveApplication).appContainer
            val storesServiceHandler = appContainer.storesServiceHandler
            val result = storesServiceHandler.retrieveFromAllStores()
            if (result is com.the_stilton_assistants.dealdetective.util.Result.Error) {
                Log.e(
                    TAG,
                    "Worker finished with an error while retrieving products: ${result.error.message}"
                )
                return Result.failure()
            }
            Log.d(TAG, "Worker finished")
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving products", e)
            return Result.failure()
        }
    }
}
