package com.the_stilton_assistants.dealdetective.repository.settings

import android.util.Log
import androidx.datastore.core.DataStore
import com.the_stilton_assistants.dealdetective.model.Settings
import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

private const val TAG = "SettingsRepository"

class SettingsRepository(
    private val storesSettingsDataStore: DataStore<Settings>
) : ISettingsRepository {
    override fun getSettingsFlow(): Result<Flow<Settings>> {
        return try {
            Result.Success(storesSettingsDataStore.data)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun getSettings(): Result<Settings> {
        return try {
            Result.Success(storesSettingsDataStore.data.first())
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun updateSettings(settings: Settings): Result<Unit> {
        return try {
            storesSettingsDataStore.updateData { settings }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }
}
