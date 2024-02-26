package com.example.a2048.domain.entity

data class Game(
    val field: Array<IntArray>,
    var score: Int = 0
)
