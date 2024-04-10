package com.example.a2048.domain.entity

import com.example.a2048.util.CellCoordinates
import com.example.a2048.util.Direction

data class Game(
    val gameMode: GameMode,
    val field: List<List<Int>>,
    val topScore: Int,
    val score: Int = 0,
    val possibleDirections: Set<Direction> = setOf(),
    val lastAddedCell: CellCoordinates? = null,
    val gameOver: Boolean = false
)
