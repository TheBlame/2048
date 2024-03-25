package com.example.a2048.util

enum class Direction {
    TOP, BOTTOM, LEFT, RIGHT;

    companion object {
        operator fun get(angle: Float): Direction {
            return when (angle) {
                in 45f..<135f -> BOTTOM

                in 0f..<45f, in 315f..<360f -> RIGHT

                in 225f..<315f -> TOP

                else -> LEFT
            }
        }
    }
}