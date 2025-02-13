package com.the_stilton_assistants.dealdetective.service.stores

/**
 * Error types for the store scraper service
 */
sealed interface ServiceError {
    val message: String

    data class UnknownError(
        override val message: String
    ) : ServiceError
}
