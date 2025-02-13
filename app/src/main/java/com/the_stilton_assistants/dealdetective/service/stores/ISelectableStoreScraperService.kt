package com.the_stilton_assistants.dealdetective.service.stores

import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineScope

/**
 * Service that provides the operations for the selectable store scraper.
 */
interface ISelectableStoreScraperService {
    suspend fun saveNewStore(
        url: String,
        appScope: CoroutineScope,
    ) : Result<Unit>
}
