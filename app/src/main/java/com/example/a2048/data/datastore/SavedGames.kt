package com.example.a2048.data.datastore

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class SavedGames(
    @Serializable(with = MyPersistentMapSerializer::class)
    val games: PersistentMap<GameMode, Game> = persistentMapOf()
)
