package com.the_stilton_assistants.dealdetective.repository.user

import com.google.firebase.auth.FirebaseUser
import com.the_stilton_assistants.dealdetective.util.Result
import org.json.JSONObject

/**
 * Repository that provides the database operations for the user.
 */
interface IUserDatabaseRepository {
    /**
     * Upload the user data to the given data source.
     */
    suspend fun uploadUserData(user: FirebaseUser, jsonObj: JSONObject): Result<Unit>

    /**
     * Retrieve the user data from the given data source.
     */
    suspend fun retrieveUserData(user: FirebaseUser): Result<JSONObject>
}
