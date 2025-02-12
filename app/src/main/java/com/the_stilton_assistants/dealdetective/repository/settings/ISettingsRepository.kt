package com.the_stilton_assistants.dealdetective.repository.settings

import com.the_stilton_assistants.dealdetective.model.Settings
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    fun getSettingsFlow(): Result<Flow<Settings>>

    suspend fun getSettings(): Result<Settings>

    suspend fun updateSettings(settings: Settings): Result<Unit>
}
