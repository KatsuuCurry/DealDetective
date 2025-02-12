package com.the_stilton_assistants.dealdetective.repository

/**
 * Error types for the repository
 */
sealed interface RepositoryError {
    val message: String

    data class UnknownError(
        override val message: String
    ) : RepositoryError
}

/**
 * Exception thrown by the repository
 */
internal class RepositoryException(
    val error: RepositoryError
) : Exception()
