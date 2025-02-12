package com.the_stilton_assistants.dealdetective.repository.user

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.the_stilton_assistants.dealdetective.util.Result

interface IUserAuthRepository {
    suspend fun getCurrentUser(): Result<FirebaseUser?>

    suspend fun signIn(email: String, password: String): Result<FirebaseUser>

    suspend fun signUp(email: String, password: String): Result<FirebaseUser>

    suspend fun signOut(): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun sendEmailVerification(): Result<Unit>

    suspend fun updateAccount(newData: UserProfileChangeRequest): Result<Unit>
}
