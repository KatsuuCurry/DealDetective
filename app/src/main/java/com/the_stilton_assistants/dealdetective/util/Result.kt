package com.the_stilton_assistants.dealdetective.util

import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.service.stores.ServiceError

/**
 * A sealed class that represents the result of an operation
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: RepositoryError) : Result<Nothing>()
    data class ServiceLayerError(val error: ServiceError) : Result<Nothing>()

    /**
     * Returns the value of the [Success] result
     */
    suspend fun onSuccess(action: suspend (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    /**
     * Returns the value of the [Error] result
     */
    suspend fun onError(action: suspend (RepositoryError) -> Unit): Result<T> {
        if (this is Error) {
            action(error)
        }
        return this
    }

    /**
     * Returns the value of the [ServiceLayerError] result
     */
    suspend fun onServiceError(action: suspend (ServiceError) -> Unit): Result<T> {
        if (this is ServiceLayerError) {
            action(error)
        }
        return this
    }
}
