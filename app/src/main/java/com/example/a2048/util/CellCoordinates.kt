package com.example.a2048.util

import kotlinx.serialization.Serializable

@Serializable
data class CellCoordinates(
    val row: Int,
    val column: Int
)
