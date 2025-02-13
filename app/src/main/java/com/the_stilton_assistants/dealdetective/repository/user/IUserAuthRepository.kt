package com.the_stilton_assistants.dealdetective.repository.user

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.the_stilton_assistants.dealdetective.util.Result

/**
 * Repository that provides the authentication operations for the user.
 */
interface IUserAuthRepository {
    /**
     * Retrieve the current user from the given data source.
     */
    suspend fun getCurrentUser(): Result<FirebaseUser?>

    /**
     * Sign in the user with the given email and password.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>

    /**
     * Sign up the user with the given email and password.
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>

    /**
     * Sign out the user from the given data source.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Send a password reset email to the given email.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Send an email verification to the current user.
     */
    suspend fun sendEmailVerification(): Result<Unit>

    /**
     * Update the current user's profile with the given data.
     */
    suspend fun updateAccount(newData: UserProfileChangeRequest): Result<Unit>
}
