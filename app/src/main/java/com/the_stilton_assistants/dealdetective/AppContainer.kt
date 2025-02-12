package com.the_stilton_assistants.dealdetective

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.repository.products.ProductsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.ISettingsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.IStoresSettingsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.SettingsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.StoresSettingsRepository
import com.the_stilton_assistants.dealdetective.repository.user.FirebaseAuthRepository
import com.the_stilton_assistants.dealdetective.repository.user.FirebaseDatabaseRepository
import com.the_stilton_assistants.dealdetective.repository.user.IUserAuthRepository
import com.the_stilton_assistants.dealdetective.repository.user.IUserDatabaseRepository
import com.the_stilton_assistants.dealdetective.service.StoresServiceHandler
import com.the_stilton_assistants.dealdetective.storage.room.ProductDatabase
import com.the_stilton_assistants.dealdetective.storage.settingsDataStore
import com.the_stilton_assistants.dealdetective.storage.storesSettingsDataStore
import com.the_stilton_assistants.dealdetective.util.WifiObserver
import com.the_stilton_assistants.dealdetective.util.WifiStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * App container for Dependency injection.
 */
interface IAppContainer {
    val settingsRepository: ISettingsRepository

    val storesSettingsRepository: IStoresSettingsRepository

    val productsRepository: IProductsRepository

    val userAuthRepository: IUserAuthRepository

    val userDatabaseRepository: IUserDatabaseRepository

    val storesServiceHandler: StoresServiceHandler

    val wifiStatusFlow: Flow<WifiStatus.Status>

    val wifiStatusMutableState: MutableStateFlow<WifiStatus.Status>

    val wifiStatusState: StateFlow<WifiStatus.Status>
        get() = wifiStatusMutableState.asStateFlow()
}

class AppContainer(private val context: Context) : IAppContainer {

    /**
     * Implementation for [ISettingsRepository]
     */
    override val settingsRepository: ISettingsRepository by lazy {
        SettingsRepository(context.settingsDataStore)
    }

    /**
     * Implementation for [IStoresSettingsRepository]
     */
    override val storesSettingsRepository: IStoresSettingsRepository by lazy {
        StoresSettingsRepository(context.storesSettingsDataStore)
    }

    /**
     * Implementation for [IProductsRepository]
     */
    override val productsRepository: IProductsRepository by lazy {
        ProductsRepository(ProductDatabase.getDatabase(context).productDao())
    }

    /**
     * Implementation for [IUserAuthRepository]
     */
    override val userAuthRepository: IUserAuthRepository by lazy {
        Firebase.auth.setLanguageCode("it")
        FirebaseAuthRepository(Firebase.auth)
    }

    /**
     * Implementation for [IUserDatabaseRepository]
     */
    override val userDatabaseRepository: IUserDatabaseRepository by lazy {
        FirebaseDatabaseRepository(Firebase.database)
    }

    /**
     * Implementation for [StoresServiceHandler]
     */
    override val storesServiceHandler: StoresServiceHandler by lazy {
        StoresServiceHandler(storesSettingsRepository, productsRepository)
    }

    /**
     * Implementation for [WifiObserver]
     */
    override val wifiStatusFlow: Flow<WifiStatus.Status> = WifiObserver(context).observe()

    /**
     * Mutable state for [WifiStatus.Status]
     */
    override val wifiStatusMutableState: MutableStateFlow<WifiStatus.Status> =
        MutableStateFlow(WifiStatus.Status.Unavailable)
}
