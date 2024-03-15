package com.example.a2048.domain.repository

import com.example.a2048.Utils.Direction
import com.example.a2048.domain.entity.Game

interface GameRepository {

    fun startGame(rows: Int, columns: Int, startingField: Array<IntArray>? = null): Game

    fun swipeFieldToDirection(game: Game, direction: Direction, testMode: Boolean = false): Game
}