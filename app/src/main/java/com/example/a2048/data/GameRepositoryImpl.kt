package com.example.a2048.data

import com.example.a2048.Utils.CellCoordinates
import com.example.a2048.Utils.Direction
import com.example.a2048.Utils.Direction.*
import com.example.a2048.Utils.Helpers.Companion.deepCopy
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor() : GameRepository {

    override fun startGame(rows: Int, columns: Int, startingField: Array<IntArray>?): Game {
        val game: Game

        if (startingField != null) {
            game = Game(startingField.deepCopy())
        } else {
            game = Game(Array(rows) { IntArray(columns) })
            repeat(2) { addNumberToField(game.field) }
            checkPossibleMoves(game)
        }

        return game
    }

    override fun swipeFieldToDirection(game: Game, direction: Direction, testMode: Boolean): Game {
        val result = game.clone()
        if (result.possibleDirections.contains(direction)) {
            moveFieldWithAddition(result, direction)

            if (!testMode) {
                addNumberToField(result.field)
            }

            result.possibleDirections.clear()
            checkPossibleMoves(result)

            if (result.possibleDirections.isEmpty()) result.gameOver = true
        }

        return result
    }

    private fun checkPossibleMoves(game: Game) {
        for (direction in Direction.entries) {
            val gameCopy = game.clone()

            when (direction) {
                BOTTOM -> if (moveFieldToBottom(gameCopy.field)) {
                    game.possibleDirections.add(direction)
                    continue
                }

                TOP -> if (moveFieldToTop(gameCopy.field)) {
                    game.possibleDirections.add(direction)
                    continue
                }

                LEFT -> if (moveFieldToLeft(gameCopy.field)) {
                    game.possibleDirections.add(direction)
                    continue
                }

                RIGHT -> if (moveFieldToRight(gameCopy.field)) {
                    game.possibleDirections.add(direction)
                    continue
                }

                else -> continue
            }

            if (addition(gameCopy, direction)) {
                game.possibleDirections.add(direction)
            }
        }
    }

    private fun addNumberToField(field: Array<IntArray>) {
        val emptyCells = checkZerosInField(field)

        if (emptyCells.isEmpty()) return

        val number = if ((1..10).random() > 1) 2 else 4
        val emptyCell = emptyCells.random()
        field[emptyCell.row][emptyCell.column] = number
    }

    private fun checkZerosInField(field: Array<IntArray>): Set<CellCoordinates> {
        val result: MutableSet<CellCoordinates> = mutableSetOf()

        field.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, column ->
                if (column == 0) {
                    result.add(CellCoordinates(rowIndex, columnIndex))
                }
            }
        }

        return result.toSet()
    }

    private fun moveFieldWithAddition(game: Game, direction: Direction, addition: Boolean = false) {
        when (direction) {
            BOTTOM -> moveFieldToBottom(game.field)

            TOP -> moveFieldToTop(game.field)

            LEFT -> moveFieldToLeft(game.field)

            RIGHT -> moveFieldToRight(game.field)

            else -> return
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
                        || (column == field[row].size - 1 && field[row][column - 1] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }

            else -> return false
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

            else -> return false
        }
        return addition
    }
}