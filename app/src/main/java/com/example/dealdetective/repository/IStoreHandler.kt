package com.example.dealdetective.repository

import com.example.application.EsselungaStore
import com.example.dealdetective.AppContainer

interface IStoreHandler {
    suspend fun getStoreDiscount(appContainer: AppContainer, storeCode: String, store: EsselungaStore) : Boolean
}