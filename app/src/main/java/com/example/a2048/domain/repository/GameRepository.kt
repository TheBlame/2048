package com.example.a2048.domain.repository

import com.example.a2048.Direction
import com.example.a2048.domain.entity.Game

interface GameRepository {

    fun startGame(rows: Int, columns: Int, startingField: Array<IntArray>? = null): Game

    fun swipeFieldToDirection(direction: Direction, testMode: Boolean = false)
}