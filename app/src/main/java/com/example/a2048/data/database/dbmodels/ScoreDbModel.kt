package com.example.a2048.data.database.dbmodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.a2048.domain.entity.GameMode

@Entity(tableName = "scores")
data class ScoreDbModel(
    val date: String,
    val score: Int,
    val mode: GameMode,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
