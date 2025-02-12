package com.the_stilton_assistants.dealdetective.repository.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

private const val TAG = "FirebaseAuthRepository"

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUserAuthRepository {
    override suspend fun getCurrentUser(): Result<FirebaseUser?> {
        return try {
            withContext(ioDispatcher) {
                val user = auth.currentUser
                Result.Success(user)
            }
        } catch (e: Exception) {
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            withContext(ioDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.user != null) {
                            Log.d(TAG, "signInWithEmail:success")
                            continuation.resume(Result.Success(task.result.user!!))
                        } else {
                            Log.e(TAG, "signInWithEmail:failure", task.exception)
                            continuation.resume(
                                Result.Error(
                                    RepositoryError.UnknownError(
                                        task.exception?.message ?: "An unknown error occurred"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            withContext(ioDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.user != null) {
                            Log.d(TAG, "createUserWithEmail:success")
                            continuation.resume(Result.Success(task.result.user!!))
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            continuation.resume(
                                Result.Error(
                                    RepositoryError.UnknownError(
                                        task.exception?.message ?: "An unknown error occurred"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                auth.signOut()
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "sendPasswordResetEmail:success")
                            continuation.resume(Result.Success(Unit))
                        } else {
                            Log.w(TAG, "sendPasswordResetEmail:failure", task.exception)
                            continuation.resume(
                                Result.Error(
                                    RepositoryError.UnknownError(
                                        task.exception?.message ?: "An unknown error occurred"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            if (auth.currentUser == null) {
                return Result.Error(RepositoryError.UnknownError("No user is currently signed in"))
            }
            withContext(ioDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    auth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "sendEmailVerification:success")
                            continuation.resume(Result.Success(Unit))
                        } else {
                            Log.w(TAG, "sendEmailVerification:failure", task.exception)
                            continuation.resume(
                                Result.Error(
                                    RepositoryError.UnknownError(
                                        task.exception?.message ?: "An unknown error occurred"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun updateAccount(newData: UserProfileChangeRequest): Result<Unit> {
        return try {
            if (auth.currentUser == null) {
                return Result.Error(RepositoryError.UnknownError("No user is currently signed in"))
            }
            withContext(ioDispatcher) {
                suspendCancellableCoroutine { continuation ->
                    auth.currentUser!!.updateProfile(newData).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "updateProfile:success")
                            continuation.resume(Result.Success(Unit))
                        } else {
                            Log.w(TAG, "updateProfile:failure", task.exception)
                            continuation.resume(
                                Result.Error(
                                    RepositoryError.UnknownError(
                                        task.exception?.message ?: "An unknown error occurred"
                                    )
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }
}
