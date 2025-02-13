package com.the_stilton_assistants.dealdetective.repository.settings

import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoresSettings
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [StoresSettings]
 */
interface IStoresSettingsRepository {
    /**
     * Retrieve the flow of the settings from the given data source.
     */
    fun getStoresSettingsFlow(): Result<Flow<StoresSettings>>

    /**
     * Retrieve the settings from the given data source.
     */
    suspend fun getStoresSettings(): Result<StoresSettings>

    /**
     * Enable a store in the given data source.
     */
    suspend fun enableStore(storeId: Int, store: Store): Result<Unit>

    /**
     * Disable a store in the given data source.
     */
    suspend fun disableStore(storeId: Int): Result<Unit>

    /**
     * Insert a store in the given data source.
     */
    suspend fun insertStore(storeId: Int, store: Store): Result<Unit>

    /**
     * Remove a store in the given data source.
     */
    suspend fun removeStore(storeId: Int): Result<Unit>

    /**
     * Update the promo code of a store in the given data source.
     */
    suspend fun updatePromoCode(storeId: Int, promoCode: Int): Result<Unit>

    /**
     * Initialize the stores settings in the given data source.
     */
    suspend fun initializeStoresSettings(storesSettings: StoresSettings): Result<Unit>
}
