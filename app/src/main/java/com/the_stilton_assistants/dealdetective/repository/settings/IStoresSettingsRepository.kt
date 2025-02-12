package com.the_stilton_assistants.dealdetective.repository.settings

import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoresSettings
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow

interface IStoresSettingsRepository {
    fun getStoresSettingsFlow(): Result<Flow<StoresSettings>>

    suspend fun getStoresSettings(): Result<StoresSettings>

    suspend fun enableStore(storeId: Int, store: Store): Result<Unit>

    suspend fun disableStore(storeId: Int): Result<Unit>

    suspend fun insertStore(storeId: Int, store: Store): Result<Unit>

    suspend fun removeStore(storeId: Int): Result<Unit>

    suspend fun updatePromoCode(storeId: Int, promoCode: Int): Result<Unit>

    suspend fun initializeStoresSettings(storesSettings: StoresSettings): Result<Unit>
}
