package com.example.a2048.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.a2048.data.database.dbmodels.ScoreDbModel
import com.example.a2048.domain.entity.GameMode

@Dao
interface DbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScore(scoreDbModel: ScoreDbModel)

    @Query("SELECT * FROM scores WHERE mode == :mode ORDER BY score DESC LIMIT 5")
    suspend fun getScoresByMode(mode: GameMode): List<ScoreDbModel>

    @Query("SELECT MAX(score) FROM scores WHERE mode == :mode")
    suspend fun getTopScoreByMode(mode: GameMode): Int?
}