package com.example.dealdetective.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.application.EsselungaStores
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object EsselungaStoresSerializer : Serializer<EsselungaStores> {
    override val defaultValue: EsselungaStores = EsselungaStores.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): EsselungaStores {
        try {
            return EsselungaStores.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: EsselungaStores,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.esselungaStoresDataStore: DataStore<EsselungaStores> by dataStore(
    fileName = "esselunga_stores.pb",
    serializer = EsselungaStoresSerializer
)