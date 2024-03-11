package com.example.a2048.presentation

typealias OnFieldChangedListener = (field: GameField) -> Unit

class GameField(
    var field: Array<IntArray>
) {
    val rows = field.size
    val columns = field[0].size

    val listeners = mutableSetOf<OnFieldChangedListener>()

    fun getCell(row: Int, column: Int): Int {
        return field[row][column]
    }

    fun setCell(row: Int, column: Int, value: Int) {
        if (field[row][column] != value) {
            field[row][column] = value
            listeners.forEach {it.invoke(this)}
        }
    }
}