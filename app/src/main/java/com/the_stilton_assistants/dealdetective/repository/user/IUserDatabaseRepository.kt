package com.the_stilton_assistants.dealdetective.repository.user

import com.google.firebase.auth.FirebaseUser
import com.the_stilton_assistants.dealdetective.util.Result
import org.json.JSONObject


interface IUserDatabaseRepository {
    suspend fun uploadUserData(user: FirebaseUser, jsonObj: JSONObject): Result<Unit>

    suspend fun retrieveUserData(user: FirebaseUser): Result<JSONObject>
}
