package com.example.dealdetective

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.application.EsselungaStores
import com.example.dealdetective.storage.esselungaStoresDataStore
import com.example.dealdetective.storage.room.ProductDatabase
import com.example.dealdetective.storage.room.ProductRepository
import com.example.dealdetective.util.WifiObserver
import com.example.dealdetective.util.WifiStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val esselungaStoresDataStore: DataStore<EsselungaStores>

    val productsRepository: ProductRepository

    val wifiStatusFlow: Flow<WifiStatus.Status>

    var _wifiStatusMutableState: MutableStateFlow<WifiStatus.Status>
    val wifiStatusState: StateFlow<WifiStatus.Status>
}

class AppDataContainer(private val context: Context) : AppContainer {

    /**
     * Implementation for [DataStore<EsselungaStores>]
     */
    override val esselungaStoresDataStore: DataStore<EsselungaStores> by lazy {
        context.esselungaStoresDataStore
    }

    /**
     * Implementation for [ProductRepository]
     */
    override val productsRepository: ProductRepository by lazy {
        ProductRepository(ProductDatabase.getDatabase(context).productDao())
    }

    /**
     * Implementation for [WifiObserver]
     */
    override val wifiStatusFlow: Flow<WifiStatus.Status> = WifiObserver(context).observe()

    /**
     * Implementation for [MutableStateFlow<WifiStatus.Status>]
     */
    override var _wifiStatusMutableState: MutableStateFlow<WifiStatus.Status> = MutableStateFlow(WifiStatus.Status.Unavailable)
    override val wifiStatusState: StateFlow<WifiStatus.Status> = _wifiStatusMutableState
}