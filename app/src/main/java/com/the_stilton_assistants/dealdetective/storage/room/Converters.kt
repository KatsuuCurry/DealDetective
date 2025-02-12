package com.the_stilton_assistants.dealdetective.storage.room

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
