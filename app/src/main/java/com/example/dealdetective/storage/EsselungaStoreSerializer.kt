package com.example.dealdetective.storage

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.application.EsselungaStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

private object EsselungaStoreSerializer : Serializer<EsselungaStore> {
    override val defaultValue: EsselungaStore = EsselungaStore.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): EsselungaStore {
        try {
            return EsselungaStore.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: EsselungaStore,
        output: OutputStream
    ) = t.writeTo(output)
}