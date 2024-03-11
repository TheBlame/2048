package com.example.a2048.data

import com.example.a2048.Direction
import com.example.a2048.Direction.*
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository

class GameRepositoryImpl : GameRepository {

    private lateinit var game: Game

    override fun startGame(rows: Int, columns: Int): Game {
        game = Game(Array(rows) { IntArray(columns) })
        repeat(2) { addNumberToField(game.field) }
        return game
    }

    override fun swipeFieldToDirection(direction: Direction) {
        moveFieldWithAddition(game, direction)
        addNumberToField(game.field)
    }

    private fun checkPossibleMoves(game: Game): Boolean {
        if (checkZerosInField(game.field)) {
            return true
        } else {
            var haveMove: Boolean
            var haveAddition = false

            for (direction in Direction.entries) {
                val gameFieldCopy = game.field.map { it.clone() }.toTypedArray()
                val gameCopy = game.copy(field = gameFieldCopy)

                haveMove = when (direction) {
                    BOTTOM -> moveFieldToBottom(gameFieldCopy)

                    TOP -> moveFieldToTop(gameFieldCopy)

                    LEFT -> moveFieldToLeft(gameFieldCopy)

                    RIGHT -> moveFieldToRight(gameFieldCopy)
                }

                if (haveMove) return true

                haveAddition = addition(gameCopy, direction)
            }
            return haveAddition
        }
    }

    private fun addNumberToField(field: Array<IntArray>) {
        val number = if ((1..10).random() > 1) 2 else 4

        var added = false

        if (checkZerosInField(field)) {
            while (!added) {
                val randomRow = (field.indices).random()
                val randomColumn = (field[0].indices).random()

                if (field[randomRow][randomColumn] == 0) {
                    field[randomRow][randomColumn] = number
                    added = true
                }
            }
        }
    }

    private fun checkZerosInField(field: Array<IntArray>): Boolean {
        var result = false

        field.iterator().forEachRemaining { it ->
            it.iterator().forEachRemaining { if (it == 0) result = true }
        }

        return result
    }

    fun showField() {
        for (row in game.field) {

            for (column in row) {
                print("|$column| ")
            }
            println()
        }
        println()
    }

    private fun moveFieldWithAddition(game: Game, direction: Direction, addition: Boolean = false) {
        when (direction) {
            BOTTOM -> moveFieldToBottom(game.field)

            TOP -> moveFieldToTop(game.field)

            LEFT -> moveFieldToLeft(game.field)

            RIGHT -> moveFieldToRight(game.field)
        }

        val moved = checkMove(game.field, direction)

        if (moved && !addition) {
            addition(game, direction)
            moveFieldWithAddition(game, direction, true)
        } else if (!moved) {
            moveFieldWithAddition(game, direction)
        }
    }

    private fun moveFieldToLeft(field: Array<IntArray>): Boolean {
        var haveMove = false

        for (row in field.indices) {
            for (column in field[row].indices) {
                if (column == field[row].size - 1) {
                    continue
                } else if (field[row][column] == 0 && field[row][column + 1] != 0) {
                    field[row][column] = field[row][column + 1]
                    field[row][column + 1] = 0
                    haveMove = true
                }
            }
        }
        return haveMove
    }

    private fun moveFieldToRight(field: Array<IntArray>): Boolean {
        var haveMove = false

        for (row in field.indices) {
            for (column in field[row].size - 1 downTo 1) {
                if (field[row][column] == 0 && field[row][column - 1] != 0) {
                    field[row][column] = field[row][column - 1]
                    field[row][column - 1] = 0
                    haveMove = true
                }
            }
        }
        return haveMove
    }

    private fun moveFieldToTop(field: Array<IntArray>): Boolean {
        var haveMove = false

        for (row in field.indices) {
            for (column in field[row].indices) {
                if (row == field.size - 1) {
                    continue
                } else if (field[row][column] == 0 && field[row + 1][column] != 0) {
                    field[row][column] = field[row + 1][column]
                    field[row + 1][column] = 0
                    haveMove = true
                }
            }
        }
        return haveMove
    }

    private fun moveFieldToBottom(field: Array<IntArray>): Boolean {
        var haveMove = false

        for (row in field.size - 1 downTo 1) {
            for (column in field[row].indices) {
                if (field[row][column] == 0 && field[row - 1][column] != 0) {
                    field[row][column] = field[row - 1][column]
                    field[row - 1][column] = 0
                    haveMove = true
                }
            }
        }
        return haveMove
    }

    private fun checkMove(field: Array<IntArray>, direction: Direction): Boolean {
        var result = true

        when (direction) {
            BOTTOM -> loop@ for (row in field.indices) {
                if (row < 1 || row == field.size - 1) continue
                for (column in field[row].indices) {
                    if ((field[row][column] != 0 && field[row + 1][column] == 0 && field[row - 1][column] == 0)
                        || (row == field.size - 1 && field[row - 1][column] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }

            TOP -> loop@ for (row in field.size - 2 downTo 1) {
                for (column in field[row].indices) {
                    if ((field[row][column] != 0 && field[row + 1][column] == 0 && field[row - 1][column] == 0)
                        || (row == 1 && field[0][column] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }

            LEFT -> loop@ for (row in field.indices) {
                for (column in field[row].size - 2 downTo 1) {
                    if ((field[row][column] != 0 && field[row][column + 1] == 0 && field[row][column - 1] == 0)
                        || (column == 1 && field[row][0] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }

            RIGHT -> loop@ for (row in field.indices) {
                for (column in field[row].indices) {
                    if (column < 1 || column == field[row].size - 1) {
                        continue
                    } else if ((field[row][column] != 0 && field[row][column + 1] == 0 && field[row][column - 1] == 0)
                        || (row == field[row].size - 1 && field[row - 1][column] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }
        }

        return result
    }

    private fun addition(game: Game, direction: Direction): Boolean {
        var addition = false

        when (direction) {
            BOTTOM ->
                for (row in game.field.size - 1 downTo 1) {
                    for (column in game.field[row].indices) {
                        if (game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row - 1][column] == game.field[row][column]) {
                            game.score += game.field[row][column] * 2
                            game.field[row][column] = game.field[row][column] * 2
                            game.field[row - 1][column] = 0
                            addition = true
                        }
                    }
                }

            TOP ->
                for (row in game.field.indices) {
                    for (column in game.field[row].indices) {
                        if (row == game.field.size - 1 || game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row + 1][column] == game.field[row][column]) {
                            game.score += game.field[row][column] * 2
                            game.field[row][column] = game.field[row][column] * 2
                            game.field[row + 1][column] = 0
                            addition = true
                        }
                    }
                }

            LEFT ->
                for (row in game.field.indices) {
                    for (column in game.field.indices) {
                        if (column == game.field[row].size - 1 || game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row][column + 1] == game.field[row][column]) {
                            game.score += game.field[row][column] * 2
                            game.field[row][column] = game.field[row][column] * 2
                            game.field[row][column + 1] = 0
                            addition = true
                        }
                    }
                }

            RIGHT ->
                for (row in game.field.indices) {
                    for (column in game.field.size - 1 downTo 1) {
                        if (game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row][column - 1] == game.field[row][column]) {
                            game.score += game.field[row][column] * 2
                            game.field[row][column] = game.field[row][column] * 2
                            game.field[row][column - 1] = 0
                            addition = true
                        }
                    }
                }
        }
        return addition
    }
}