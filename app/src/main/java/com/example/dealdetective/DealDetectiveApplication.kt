package com.example.dealdetective

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DealDetectiveApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
        private set

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        applicationScope.launch {
            container.wifiStatusFlow.collect {
                container._wifiStatusMutableState.update { it }
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        // Cancel the application scope to clean up resources
        applicationScope.cancel()
    }
}