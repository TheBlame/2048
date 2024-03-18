package com.example.a2048.domain.repository

import com.example.a2048.util.Direction
import com.example.a2048.domain.entity.Game

interface GameRepository {

    fun startGame(rows: Int, columns: Int, startingField: List<List<Int>>? = null): Game

    fun swipeFieldToDirection(game: Game, direction: Direction, testMode: Boolean = false): Game
}