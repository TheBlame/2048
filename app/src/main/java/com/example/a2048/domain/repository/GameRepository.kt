package com.example.a2048.domain.repository

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.entity.GameScore
import com.example.a2048.util.Direction

interface GameRepository {

    suspend fun startGame(gameMode: GameMode, startingField: List<List<Int>>? = null): Game

    fun swipeFieldToDirection(game: Game, direction: Direction, testMode: Boolean = false): Game

    suspend fun saveScore(game: Game)

    suspend fun getScoresByMode(gameMode: GameMode): List<GameScore>
}