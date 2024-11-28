package com.example.dealdetective.storage.room

import androidx.room.TypeConverter
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromString(value: String): JSONObject {
        return JSONObject(value)
    }

    @TypeConverter
    fun jsonToString(json: JSONObject): String {
        return json.toString()
    }
}