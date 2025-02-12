package com.the_stilton_assistants.dealdetective.repository.user

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.the_stilton_assistants.dealdetective.repository.RepositoryError
import com.the_stilton_assistants.dealdetective.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume

private const val TAG = "FirebaseDatabaseRepository"

class FirebaseDatabaseRepository(
    private val firebaseDatabase: FirebaseDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
): IUserDatabaseRepository {
    override suspend fun uploadUserData(user: FirebaseUser, jsonObj: JSONObject): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val ref = firebaseDatabase.getReference("users/${user.uid}")
                suspendCancellableCoroutine { continuation ->
                    ref.setValue(jsonObj.toString()).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Data uploaded successfully")
                            continuation.resume(Result.Success(Unit))
                        } else {
                            Log.e(TAG, "An unknown error occurred", task.exception)
                            continuation.resume(Result.Error(RepositoryError.UnknownError("An unknown error occurred")))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }

    override suspend fun retrieveUserData(user: FirebaseUser): Result<JSONObject> {
        return try {
            withContext(ioDispatcher) {
                val ref = firebaseDatabase.getReference("users/${user.uid}")
                suspendCancellableCoroutine { continuation ->
                    ref.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val data = task.result.value as String
                            val jsonObj = JSONObject(data)
                            continuation.resume(Result.Success(jsonObj))
                        } else {
                            Log.e(TAG, "An unknown error occurred", task.exception)
                            continuation.resume(Result.Error(RepositoryError.UnknownError("An unknown error occurred")))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unknown error occurred", e)
            Result.Error(RepositoryError.UnknownError("An unknown error occurred"))
        }
    }
}
