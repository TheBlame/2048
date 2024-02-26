package com.example.a2048.data

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository

class GameRepositoryImpl: GameRepository {

    private lateinit var game: Game

    override fun startGame(rows: Int, columns: Int): Game {
        game = Game(Array(rows) { IntArray(columns) })
        return game
    }

    override fun swipeLeft(): Game {
        moveFieldToLeft()
        return game
    }

    override fun swipeRight(): Game {
        moveFieldToRight()
        return game
    }

    override fun swipeTop(): Game {
        moveFieldToTop()
        return game
    }

    override fun swipeBottom(): Game {
        moveFieldToBottom()
        return game
    }

    fun generateField() {
        repeat(15) {
            var column = (0..<game.field.size).random()
            var row = (0..<game.field[0].size).random()

            while (game.field[column][row] == 0) {
                game.field[column][row] = 2
                column = (0..<game.field.size).random()
                row = (0..<game.field[0].size).random()

            }
        }

//        game.field[0][0] = 2
//        game.field[0][1] = 2
//        game.field[0][2] = 2
//        game.field[0][3] = 2
//
//        game.field[1][0] = 2
//        game.field[1][1] = 2
//        game.field[1][2] = 2
//        game.field[1][3] = 2
//
//        game.field[0][0] = 2
//        game.field[0][0] = 2
//        game.field[0][0] = 2
//        game.field[0][0] = 2
//
//        game.field[3][0] = 2
//        game.field[3][1] = 2
//        game.field[3][2] = 2
//        game.field[3][3] = 2
    }

    fun showField() {
        for (row in game.field) {

            for (column in row) {
                print(column)
            }
            println()
        }
        println()
    }

    private fun moveFieldToLeft(recurse: Boolean = false) {
        var stuck = false
        for (row in game.field.size - 1 downTo 0) {
            for (column in game.field[row].size - 1 downTo 0) {
                if (column != 0 && game.field[row][column - 1] == game.field[row][column] && !recurse) {
                    game.score += game.field[row][column] * 2
                    game.field[row][column - 1] = game.field[row][column] * 2
                    game.field[row][column] = 0
                } else if (
                    column >= 2 && game.field[row][column - 1] != game.field[row][column]
                    && game.field[row][column - 1] != 0
                    && game.field[row][column] != 0
                ) {
                    stuck = true
                } else if (column == 0) {
                    break
                } else if (game.field[row][column - 1] == 0 && game.field[row][column] != 0) {
                    game.field[row][column - 1] = game.field[row][column]
                    game.field[row][column] = 0
                }
            }
        }
        if (stuck) {
            moveFieldToLeft(true)
        }
    }

    private fun moveFieldToRight(recurse: Boolean = false) {
        var stuck = false
        for (row in game.field.indices) {
            for (column in game.field[row].indices) {
                if (column != game.field[row].size - 1 && game.field[row][column + 1] == game.field[row][column] && !recurse) {
                    game.score += game.field[row][column] * 2
                    game.field[row][column + 1] = game.field[row][column] * 2
                    game.field[row][column] = 0
                } else if (
                    column <= 1 && game.field[row][column + 1] != game.field[row][column]
                    && game.field[row][column + 1] != 0
                    && game.field[row][column] != 0
                ) {
                    stuck = true
                } else if (column == game.field[row].size - 1) {
                    break
                } else if (game.field[row][column + 1] == 0 && game.field[row][column] != 0) {
                    game.field[row][column + 1] = game.field[row][column]
                    game.field[row][column] = 0
                }
            }
        }
        if (stuck) {
            moveFieldToRight(true)
        }
    }

    private fun moveFieldToTop(recurse: Boolean = false) {
        var stuck = false
        for (row in game.field.size - 1 downTo 0) {
            for (column in game.field[row].indices) {
                if (row != 0 && game.field[row - 1][column] == game.field[row][column] && !recurse) {
                    game.score += game.field[row][column] * 2
                    game.field[row - 1][column] = game.field[row][column] * 2
                    game.field[row][column] = 0
                } else if (
                    row >= 2 && game.field[row - 1][column] != game.field[row][column]
                    && game.field[row - 1][column] != 0
                    && game.field[row][column] != 0
                ) {
                    stuck = true
                } else if (row == 0) {
                    break
                } else if (game.field[row - 1][column] == 0 && game.field[row][column] != 0) {
                    game.field[row - 1][column] = game.field[row][column]
                    game.field[row][column] = 0
                }
            }
        }
        if (stuck) {
            moveFieldToTop(true)
        }
    }

    private fun moveFieldToBottom(recurse: Boolean = false) {
        var stuck = false
        for (row in game.field.indices) {
            for (column in game.field[row].indices) {
                if (row != game.field.size - 1 && game.field[row + 1][column] == game.field[row][column] && !recurse) {
                    game.score += game.field[row][column] * 2
                    game.field[row + 1][column] = game.field[row][column] * 2
                    game.field[row][column] = 0
                } else if (
                    row <= 1 && game.field[row + 1][column] != game.field[row][column]
                    && game.field[row + 1][column] != 0
                    && game.field[row][column] != 0
                ) {
                    stuck = true
//                } else if (row == game.field.size + 1) {
//                    break
                } else if (row <= game.field.size - 2 && game.field[row + 1][column] == 0 && game.field[row][column] != 0) {
                    game.field[row + 1][column] = game.field[row][column]
                    game.field[row][column] = 0
                }
            }
        }
        if (stuck) {
            moveFieldToBottom(true)
        }
    }
}

fun main() {
    var gr = GameRepositoryImpl()
    gr.startGame(4, 4)
    gr.generateField()
    gr.showField()
    gr.swipeBottom()
    gr.showField()
}