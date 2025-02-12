package com.the_stilton_assistants.dealdetective.service.stores

sealed interface ServiceError {
    val message: String

    data class UnknownError(
        override val message: String
    ) : ServiceError
}
