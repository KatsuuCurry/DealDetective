package com.the_stilton_assistants.dealdetective.repository.settings

import com.the_stilton_assistants.dealdetective.model.Settings
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Settings]
 * from a given data source.
 */
interface ISettingsRepository {
    /**
     * Retrieve the flow of the settings from the given data source.
     */
    fun getSettingsFlow(): Result<Flow<Settings>>

    /**
     * Retrieve the settings from the given data source.
     */
    suspend fun getSettings(): Result<Settings>

    /**
     * Update the settings in the given data source.
     */
    suspend fun updateSettings(settings: Settings): Result<Unit>
}
