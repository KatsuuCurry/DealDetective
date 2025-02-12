package com.the_stilton_assistants.dealdetective

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.the_stilton_assistants.dealdetective.worker.ProductsRetrieverWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DealDetectiveApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var appContainer: IAppContainer
        private set

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)

        applicationScope.launch {
            appContainer.storesServiceHandler.initializeHandlers()
        }

        applicationScope.launch(Dispatchers.IO) {
            appContainer.wifiStatusFlow.collect { status ->
                appContainer.wifiStatusMutableState.value = status
            }
        }

        val constraints: Constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val retrieveProducts =
            PeriodicWorkRequestBuilder<ProductsRetrieverWorker>(1, TimeUnit.DAYS)
                .addTag("DealDetectiveRetrieving")
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "DealDetectiveRetrieving",
                ExistingPeriodicWorkPolicy.UPDATE,
                retrieveProducts
            )
    }

    override fun onTerminate() {
        super.onTerminate()
        // Cancel the application scope to clean up resources
        applicationScope.cancel()
    }
}
