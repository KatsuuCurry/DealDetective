package com.the_stilton_assistants.dealdetective.repository.settings

import android.util.Log
import androidx.datastore.core.DataStore
import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoreSettings
import com.the_stilton_assistants.dealdetective.model.StoreType
import com.the_stilton_assistants.dealdetective.model.StoresSettings
import com.the_stilton_assistants.dealdetective.model.storeOrNull
import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.repository.RepositoryException
import com.the_stilton_assistants.dealdetective.repository.StoresSettingsErrors.*
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private const val TAG = "StoresSettingsRepository"

class StoresSettingsRepository(
    private val storesSettingsDataStore: DataStore<StoresSettings>
) : IStoresSettingsRepository {
    override fun getStoresSettingsFlow(): Result<Flow<StoresSettings>> {
        return try {
            runBlocking {
                checkInitialization()
            }
            Result.Success(storesSettingsDataStore.data)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun getStoresSettings(): Result<StoresSettings> {
        return try {
            Result.Success(storesSettingsDataStore.data.first())
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun enableStore(storeId: Int, store: Store): Result<Unit> {
        return try {
            checkInitialization()
            storesSettingsDataStore.updateData { storesSettings ->
                val builder = storesSettings.toBuilder()
                val storeSettings = builder.storesSettingsMap[storeId]
                checkCorrectType(storeSettings, StoreType.Toggle)

                val newStoreSettings = storeSettings!!.toBuilder().setStore(store).build()
                builder.putStoresSettings(storeId, newStoreSettings)
                builder.build()
            }
            Result.Success(Unit)
        } catch (e: RepositoryException) {
            Log.e(TAG, "An error occurred", e)
            Result.Error(e.error)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun disableStore(storeId: Int): Result<Unit> {
        return try {
            checkInitialization()
            storesSettingsDataStore.updateData { storesSettings ->
                val builder = storesSettings.toBuilder()
                val storeSettings = builder.storesSettingsMap[storeId]
                checkCorrectType(storeSettings, StoreType.Toggle)

                val newStoreSettings = storeSettings!!.toBuilder().clearStore().build()
                builder.putStoresSettings(storeId, newStoreSettings)
                builder.build()
            }
            Result.Success(Unit)
        } catch (e: RepositoryException) {
            Log.e(TAG, "An error occurred", e)
            Result.Error(e.error)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun insertStore(storeId: Int, store: Store): Result<Unit> {
        return try {
            checkInitialization()
            storesSettingsDataStore.updateData { storesSettings ->
                val builder = storesSettings.toBuilder()
                val storeSettings = builder.storesSettingsMap[storeId]
                checkCorrectType(storeSettings, StoreType.Selectable)

                val newStoreSettings = storeSettings!!.toBuilder().setStore(store).build()
                builder.putStoresSettings(storeId, newStoreSettings)
                builder.build()
            }
            Result.Success(Unit)
        } catch (e: RepositoryException) {
            Log.e(TAG, "An error occurred", e)
            Result.Error(e.error)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun removeStore(storeId: Int): Result<Unit> {
        return try {
            checkInitialization()
            storesSettingsDataStore.updateData { storesSettings ->
                val builder = storesSettings.toBuilder()
                val storeSettings = builder.storesSettingsMap[storeId]
                checkCorrectType(storeSettings, StoreType.Selectable)


                val newStoreSettings = storeSettings!!.toBuilder().clearStore().build()
                builder.putStoresSettings(storeId, newStoreSettings)
                builder.build()
            }
            Result.Success(Unit)
        } catch (e: RepositoryException) {
            Log.e(TAG, "An error occurred", e)
            Result.Error(e.error)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun updatePromoCode(storeId: Int, promoCode: Int): Result<Unit> {
        return try {
            checkInitialization()
            storesSettingsDataStore.updateData { storesSettings ->
                val builder = storesSettings.toBuilder()
                val storeSettings = builder.storesSettingsMap[storeId]
                if (storeSettings == null) {
                    throw RepositoryException(StoreSettingsNotFound)
                }

                val store = storeSettings.storeOrNull
                if (store == null) {
                    throw RepositoryException(StoreSettingsNotFound)
                }
                val newStore = store.toBuilder().setCodPromo(promoCode.toInt()).build()
                val newStoreSettings = storeSettings.toBuilder().setStore(newStore).build()
                builder.putStoresSettings(storeId, newStoreSettings)
                builder.build()
            }
            Result.Success(Unit)
        } catch (e: RepositoryException) {
            Log.e(TAG, "An error occurred", e)
            Result.Error(e.error)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun initializeStoresSettings(storesSettings: StoresSettings): Result<Unit> {
        return try {
            storesSettingsDataStore.updateData { storesSettings }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    suspend fun checkInitialization() {
        if (storesSettingsDataStore.data.first() == StoresSettings.getDefaultInstance())
            throw RepositoryException(StoreSettingsNotInitialized)
    }

    fun checkCorrectType(storeSettings: StoreSettings?, expectedType: StoreType) {
        if (storeSettings == null) {
            throw RepositoryException(StoreSettingsNotFound)
        }
        if (storeSettings.type != expectedType) {
            throw RepositoryException(IncorrectStoreType)
        }
    }
}
