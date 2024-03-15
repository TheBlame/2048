package com.example.a2048.domain.entity

import com.example.a2048.Utils.Direction
import com.example.a2048.Utils.Helpers.Companion.deepCopy

data class Game(
    val field: Array<IntArray>,
    var score: Int = 0,
    val possibleDirections: MutableSet<Direction> = mutableSetOf(),
    var gameOver: Boolean = false
) : Cloneable {

    public override fun clone(): Game =
        Game(field.deepCopy(), score, possibleDirections.toMutableSet(), gameOver)

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
