package com.example.a2048.domain.entity

data class Game(
    val field: Array<IntArray>,
    var score: Int = 0,
    var gameOver: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (!field.contentDeepEquals(other.field)) return false
        if (score != other.score) return false
        return gameOver == other.gameOver
    }

    override fun hashCode(): Int {
        var result = field.contentDeepHashCode()
        result = 31 * result + score
        result = 31 * result + gameOver.hashCode()
        return result
    }
}
