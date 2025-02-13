package com.the_stilton_assistants.dealdetective.service

import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.model.StoreSettings
import com.the_stilton_assistants.dealdetective.model.StoreType
import com.the_stilton_assistants.dealdetective.model.StoresSettings
import com.the_stilton_assistants.dealdetective.model.storeOrNull
import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.repository.products.IProductsRepository
import com.the_stilton_assistants.dealdetective.repository.settings.IStoresSettingsRepository
import com.the_stilton_assistants.dealdetective.service.stores.CarrefourCategory
import com.the_stilton_assistants.dealdetective.service.stores.CarrefourScraperService
import com.the_stilton_assistants.dealdetective.service.stores.EsselungaCategory
import com.the_stilton_assistants.dealdetective.service.stores.EsselungaScraperService
import com.the_stilton_assistants.dealdetective.service.stores.ISelectableStoreScraperService
import com.the_stilton_assistants.dealdetective.service.stores.IStoreScraperService
import com.the_stilton_assistants.dealdetective.service.stores.ServiceError
import com.the_stilton_assistants.dealdetective.service.stores.TigrosCategory
import com.the_stilton_assistants.dealdetective.service.stores.TigrosScraperService
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.yield

const val VERSION = 1

/**
 * Service handler for the stores
 */
class StoresServiceHandler(
    private val storesSettingsRepository: IStoresSettingsRepository,
    private val productsRepository: IProductsRepository,
) {

    private val _storesRetrievingMutableState =
        MutableStateFlow<StoresRetrievingState>(StoresRetrievingState.Idle)
    val storesRetrievingState: StateFlow<StoresRetrievingState> =
        _storesRetrievingMutableState.asStateFlow()

    lateinit var scrapers: Map<Int, IStoreScraperService>
        private set

    suspend fun initializeHandlers() {
        if (this::scrapers.isInitialized) {
            return
        }

        val esselungaScraperService = EsselungaScraperService(
            storesSettingsRepository,
            productsRepository,
        )

        val carrefourScraperService = CarrefourScraperService(
            storesSettingsRepository,
            productsRepository,
        )

        val tigrosScraperService = TigrosScraperService(
            storesSettingsRepository,
            productsRepository,
        )

        scrapers = mapOf(
            StoreId.ESSELUNGA.value to esselungaScraperService,
            StoreId.CARREFOUR.value to carrefourScraperService,
            StoreId.TIGROS.value to tigrosScraperService,
        )

        val dataResult = storesSettingsRepository.getStoresSettings()

        val data = (dataResult as Result.Success).data

        if (data.version == VERSION) {
            return
        }

        val builder = StoresSettings.newBuilder()
        scrapers.values.forEach {
            val storeSetting = StoreSettings.newBuilder()
                .setType(it.storeType)
                .clearStore()
                .build()
            builder.putStoresSettings(it.storeId.value, storeSetting)
        }
        builder.setVersion(VERSION)
        storesSettingsRepository.initializeStoresSettings(builder.build())
    }

    suspend fun saveNewStore(
        storeId: StoreId,
        url: String,
        appScope: CoroutineScope,
    ): Result<Unit> {
        val scraper = scrapers[storeId.value]
            ?: return Result.ServiceLayerError(ServiceError.UnknownError("Store not found"))

        if (scraper.storeType != StoreType.Selectable) {
            return Result.ServiceLayerError(ServiceError.UnknownError("Store wrong type"))
        }

        if (scraper !is ISelectableStoreScraperService) {
            return Result.ServiceLayerError(ServiceError.UnknownError("Store wrong type"))
        }
        val result =  scraper.saveNewStore(url, appScope)
        if (result !is Result.Success) {
            return result
        }
        val discountResult = appScope.async {
            retrieveProducts(
                storeId.value,
            )
        }

        return discountResult.await()
    }

    suspend fun retrieveFromAllStores(): Result<Unit> {
        synchronized(this) {
            if (_storesRetrievingMutableState.value == StoresRetrievingState.Running) {
                return Result.Success(Unit)
            }
            _storesRetrievingMutableState.value = StoresRetrievingState.Running
        }

        val storesSettingsResult = storesSettingsRepository.getStoresSettings()
        if (storesSettingsResult is Result.Error) {
            _storesRetrievingMutableState.value = StoresRetrievingState.Idle
            return storesSettingsResult
        }
        val storesSettings = (storesSettingsResult as Result.Success).data
        scrapers.values.forEach {
            val storeSettings = storesSettings.storesSettingsMap[it.storeId.value]
            require(storeSettings != null) { "Store Settings not found" }
            if (storeSettings.storeOrNull != null) {
                val result = retrieveProducts(
                    it.storeId.value,
                )
                if (result is Result.Error) {
                    _storesRetrievingMutableState.value = StoresRetrievingState.Idle
                    return result
                }
            }
        }
        _storesRetrievingMutableState.value = StoresRetrievingState.Idle
        return Result.Success(Unit)
    }

    suspend fun retrieveSingleProducts(
        storeId: Int,
        store: Store? = null,
        appScope: CoroutineScope,
    ): Result<Unit> {
        val result = appScope.async {
            var run = true
            while (true) {
                synchronized(this) {
                    if (_storesRetrievingMutableState.value != StoresRetrievingState.Running) {
                        _storesRetrievingMutableState.value = StoresRetrievingState.Running
                        run = false
                    }
                }
                if (!run) {
                    break
                }
                delay(100)
                yield()
            }
            val result = retrieveProducts(
                storeId,
                store,
            )
            if (result !is Result.Success) {
                _storesRetrievingMutableState.value = StoresRetrievingState.Idle
                return@async result
            }
            _storesRetrievingMutableState.value = StoresRetrievingState.Idle
            return@async Result.Success(Unit)
        }
        return result.await()
    }

    private suspend fun retrieveProducts(
        storeId: Int,
        store: Store? = null,
    ): Result<Unit> {
        val storeSettingsResult = storesSettingsRepository.getStoresSettings()
        if (storeSettingsResult is Result.Error) {
            return storeSettingsResult
        }
        val storeSettings = (storeSettingsResult as Result.Success).data.storesSettingsMap[storeId]

        require(storeSettings != null) { "Store Settings not found" }

        val scraper = scrapers[storeId]
        if (scraper == null) {
            return Result.ServiceLayerError(ServiceError.UnknownError("Store not found"))
        }
        var result: Result<Int?>
        if (store == null) {
            result = scraper.getStoreDiscount(
                storeSettings.store,
            )
            if (result is Result.Error || result is Result.ServiceLayerError) {
                return result
            }
        } else {
            result = scraper.getStoreDiscount(
                store,
            )
            if (result is Result.Error || result is Result.ServiceLayerError) {
                return result
            }
            if (scraper.storeType == StoreType.Selectable) {
                return Result.Error(RepositoryError.UnknownError("Store wrong type"))
            }
            val resultStore = storesSettingsRepository.enableStore(storeId, store)
                .onError {
                    productsRepository.deleteProductsByStoreType(storeId)
                }
            if (resultStore is Result.Error) {
                return resultStore
            }
        }
        val promoCode = (result as Result.Success).data
        promoCode?.let {
            return storesSettingsRepository.updatePromoCode(storeId, it)
        }
        return Result.Success(Unit)
    }

    fun getStoreCategory(storeId: StoreId): List<Pair<String, String>> {
        return when (storeId) {
            StoreId.ESSELUNGA -> EsselungaCategory.Companion.categories
            StoreId.CARREFOUR -> CarrefourCategory.Companion.categories
            StoreId.TIGROS -> TigrosCategory.Companion.categories
            else -> throw IllegalArgumentException("Store not found")
        }
    }
}

sealed interface StoresRetrievingState {
    object Idle : StoresRetrievingState
    object Running : StoresRetrievingState
}
