package com.example.a2048.data.datastore

import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SavedGamesSerializer : Serializer<SavedGames> {
    override val defaultValue: SavedGames
        get() = SavedGames()

    override suspend fun readFrom(input: InputStream): SavedGames {
        return try {
            Json.decodeFromString(
                deserializer = SavedGames.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: SavedGames, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(
                    serializer = SavedGames.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}