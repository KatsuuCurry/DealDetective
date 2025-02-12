package com.the_stilton_assistants.dealdetective.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.the_stilton_assistants.dealdetective.model.StoresSettings
import java.io.InputStream
import java.io.OutputStream

object StoresSettingsSerializer: Serializer<StoresSettings> {
    override val defaultValue: StoresSettings = StoresSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StoresSettings {
        try {
            return StoresSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: StoresSettings,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.storesSettingsDataStore: DataStore<StoresSettings> by dataStore(
    fileName = "stores_settings.pb",
    serializer = StoresSettingsSerializer
)
