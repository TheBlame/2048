package com.example.a2048.presentation

import com.example.a2048.domain.entity.Game

class GameField(
    private val field: Array<IntArray>
) {
    private val rows = field.size
    private val columns = field[0].size
}