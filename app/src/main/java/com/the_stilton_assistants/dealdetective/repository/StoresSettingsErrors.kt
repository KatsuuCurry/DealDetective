package com.the_stilton_assistants.dealdetective.repository

/**
 * Error types for the stores settings repository
 */
internal sealed interface StoresSettingsErrors : RepositoryError {
    object StoreSettingsNotFound : StoresSettingsErrors {
        override val message = "Store Settings not found"
    }
    object StoreSettingsNotInitialized : StoresSettingsErrors {
        override val message = "Store Settings not initialized"
    }
    object IncorrectStoreType : StoresSettingsErrors {
        override val message = "Incorrect store type"
    }
}
