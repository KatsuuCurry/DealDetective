package com.the_stilton_assistants.dealdetective.service.stores

import com.the_stilton_assistants.dealdetective.model.Store
import com.the_stilton_assistants.dealdetective.model.StoreId
import com.the_stilton_assistants.dealdetective.model.StoreType
import com.the_stilton_assistants.dealdetective.util.Result

/**
 * Service that provides the operations for the store scraper.
 */
interface IStoreScraperService {
    val storeId: StoreId
    val storeType: StoreType

    suspend fun getStoreDiscount(
        store: Store,
    ) : Result<Int?>

    sealed interface BaseErrors: ServiceError {
        object IncorrectGetResponse : BaseErrors {
            override val message: String = "The response from the GET request was not 200"
        }
    }
}
