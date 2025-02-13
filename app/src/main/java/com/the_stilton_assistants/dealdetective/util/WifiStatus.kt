package com.the_stilton_assistants.dealdetective.util

import kotlinx.coroutines.flow.Flow

/**
 * An interface that provides the status of the wifi connection
 */
interface WifiStatus {

    /**
     * Observe the status of the wifi connection
     *
     * @return A flow of the status of the wifi connection
     */
    fun observe(): Flow<Status>

    /**
     * An enum class that represents the status of the wifi connection
     */
    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
