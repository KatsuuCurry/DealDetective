package com.example.dealdetective.util

import kotlinx.coroutines.flow.Flow

interface WifiStatus {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}