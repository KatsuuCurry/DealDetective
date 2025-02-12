package com.the_stilton_assistants.dealdetective.service.stores

import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineScope

interface ISelectableStoreScraperService {
    suspend fun saveNewStore(
        url: String,
        appScope: CoroutineScope,
    ) : Result<Unit>
}
