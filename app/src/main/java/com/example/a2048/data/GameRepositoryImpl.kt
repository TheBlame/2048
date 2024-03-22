package com.example.a2048.data

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository
import com.example.a2048.util.CellCoordinates
import com.example.a2048.util.Direction
import com.example.a2048.util.Direction.BOTTOM
import com.example.a2048.util.Direction.LEFT
import com.example.a2048.util.Direction.RIGHT
import com.example.a2048.util.Direction.TOP
import com.example.a2048.util.Helpers.Companion.twoDimensionalListToMutableList
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor() : GameRepository {

    override fun startGame(rows: Int, columns: Int, startingField: List<List<Int>>?): Game {
        var game: Game

        if (startingField != null) {
            game = Game(startingField)
        } else {
            val list = buildList {
                repeat(rows) {
                    add(buildList { repeat(columns) { add(0) } })
                }
            }

            game = Game(list)
            repeat(2) { game = addNumberToField(game) }

        }

        val possibleMoves = checkPossibleMoves(game)

        return game.copy(possibleDirections = possibleMoves)
    }

    override fun swipeFieldToDirection(game: Game, direction: Direction, testMode: Boolean): Game {
        if (!game.possibleDirections.contains(direction)) return game

        var newGameState = moveFieldWithAddition(game, direction)

        if (!testMode) {
            newGameState = addNumberToField(newGameState)
        }

        val possibleMoves = checkPossibleMoves(newGameState)

        if (possibleMoves.isEmpty()) return newGameState.copy(
            possibleDirections = possibleMoves,
            gameOver = true
        )

        return newGameState.copy(
            possibleDirections = possibleMoves
        )
    }

    private fun checkPossibleMoves(game: Game): Set<Direction> {
        val set = mutableSetOf<Direction>()
        for (direction in Direction.entries) {

            when (direction) {
                BOTTOM -> if (checkMoveToBottom(game.field)) {
                    set.add(direction)
                    continue
                }

                TOP -> if (checkMoveToTop(game.field)) {
                    set.add(direction)
                    continue
                }

                LEFT -> if (checkMoveToLeft(game.field)) {
                    set.add(direction)
                    continue
                }

                RIGHT -> if (checkMoveToRight(game.field)) {
                    set.add(direction)
                    continue
                }

                else -> continue
            }

            if (checkAddition(game, direction)) {
                set.add(direction)
            }
        }

        return set.toSet()
    }

    private fun addNumberToField(game: Game): Game {
        val emptyCells = checkZerosInField(game.field)

        if (emptyCells.isEmpty()) return game

        val field = game.field.twoDimensionalListToMutableList()

        val number = if ((1..10).random() > 1) 2 else 4
        val emptyCell = emptyCells.random()
        field[emptyCell.row][emptyCell.column] = number
        return game.copy(field = field, lastAddedCell = emptyCell)
    }

    private fun checkZerosInField(field: List<List<Int>>): Set<CellCoordinates> {
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

    private fun moveFieldWithAddition(
        game: Game,
        direction: Direction,
        addition: Boolean = false
    ): Game {
        val newGameState = when (direction) {
            BOTTOM -> moveFieldToBottom(game)

            TOP -> moveFieldToTop(game)

            LEFT -> moveFieldToLeft(game)

            RIGHT -> moveFieldToRight(game)

            else -> return game
        }

        val moved = checkMove(newGameState.field, direction)

        if (moved && !addition) {
            val gameAfterAddition = addition(newGameState, direction)
            return moveFieldWithAddition(gameAfterAddition, direction, true)
        } else if (!moved) {
            return moveFieldWithAddition(newGameState, direction)
        }

        return newGameState
    }

    private fun moveFieldToLeft(game: Game): Game {
        val field = game.field.twoDimensionalListToMutableList()

        for (row in field.indices) {
            for (column in field[row].indices) {
                if (column == field[row].size - 1) {
                    continue
                } else if (field[row][column] == 0 && field[row][column + 1] != 0) {
                    field[row][column] = field[row][column + 1]
                    field[row][column + 1] = 0
                }
            }
        }

        return game.copy(field = field)
    }

    private fun checkMoveToLeft(field: List<List<Int>>): Boolean {
        for (row in field.indices) {
            for (column in field[row].indices) {
                if (column == field[row].size - 1) {
                    continue
                } else if (field[row][column] == 0 && field[row][column + 1] != 0) {
                    return true
                }
            }
        }

        return false
    }

    private fun moveFieldToRight(game: Game): Game {
        val field = game.field.twoDimensionalListToMutableList()

        for (row in field.indices) {
            for (column in field[row].size - 1 downTo 1) {
                if (field[row][column] == 0 && field[row][column - 1] != 0) {
                    field[row][column] = field[row][column - 1]
                    field[row][column - 1] = 0
                }
            }
        }

        return game.copy(field = field)
    }

    private fun checkMoveToRight(field: List<List<Int>>): Boolean {
        for (row in field.indices) {
            for (column in field[row].size - 1 downTo 1) {
                if (field[row][column] == 0 && field[row][column - 1] != 0) {
                    return true
                }
            }
        }

        return false
    }

    private fun moveFieldToTop(game: Game): Game {
        val field = game.field.twoDimensionalListToMutableList()

        for (row in field.indices) {
            for (column in field[row].indices) {
                if (row == field.size - 1) {
                    continue
                } else if (field[row][column] == 0 && field[row + 1][column] != 0) {
                    field[row][column] = field[row + 1][column]
                    field[row + 1][column] = 0
                }
            }
        }

        return game.copy(field = field)
    }

    private fun checkMoveToTop(field: List<List<Int>>): Boolean {
        for (row in field.indices) {
            for (column in field[row].indices) {
                if (row == field.size - 1) {
                    continue
                } else if (field[row][column] == 0 && field[row + 1][column] != 0) {
                    return true
                }
            }
        }

        return false
    }

    private fun moveFieldToBottom(game: Game): Game {
        val field = game.field.twoDimensionalListToMutableList()

        for (row in field.size - 1 downTo 1) {
            for (column in field[row].indices) {
                if (field[row][column] == 0 && field[row - 1][column] != 0) {
                    field[row][column] = field[row - 1][column]
                    field[row - 1][column] = 0
                }
            }
        }

        return game.copy(field = field)
    }

    private fun checkMoveToBottom(field: List<List<Int>>): Boolean {
        for (row in field.size - 1 downTo 1) {
            for (column in field[row].indices) {
                if (field[row][column] == 0 && field[row - 1][column] != 0) {
                    return true
                }
            }
        }

        return false
    }

    private fun checkMove(field: List<List<Int>>, direction: Direction): Boolean {
        var result = true

        when (direction) {
            BOTTOM -> loop@ for (row in field.indices) {
                if (row < 1 || row == field.size - 1) continue
                for (column in field[row].indices) {
                    if ((field[row][column] == 0 && field[row - 1][column] != 0)
                        || (field[row][column] != 0 && field[row + 1][column] == 0 && field[row - 1][column] == 0)
                        || (row == field.size - 1 && field[row - 1][column] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }

            TOP -> loop@ for (row in field.size - 2 downTo 1) {
                for (column in field[row].indices) {
                    if ((field[row][column] == 0 && field[row + 1][column] != 0)
                        || (field[row][column] != 0 && field[row + 1][column] == 0 && field[row - 1][column] == 0)
                        || (row == 1 && field[0][column] == 0 && field[row][column] != 0)
                    ) {
                        result = false
                        break@loop
                    }
                }
            }

            LEFT -> loop@ for (row in field.indices) {
                for (column in field[row].size - 2 downTo 1) {
                    if ((field[row][column] == 0 && field[row][column + 1] != 0)
                        || (field[row][column] != 0 && field[row][column + 1] == 0 && field[row][column - 1] == 0)
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
                    } else if ((field[row][column] == 0 && field[row][column - 1] != 0)
                        || (field[row][column] != 0 && field[row][column + 1] == 0 && field[row][column - 1] == 0)
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

    private fun addition(game: Game, direction: Direction): Game {
        val field = game.field.twoDimensionalListToMutableList()
        var score = game.score

        when (direction) {
            BOTTOM ->
                for (row in field.size - 1 downTo 1) {
                    for (column in field[row].indices) {
                        if (field[row][column] == 0) {
                            continue
                        } else if (field[row - 1][column] == field[row][column]) {
                            score += field[row][column] * 2
                            field[row][column] = field[row][column] * 2
                            field[row - 1][column] = 0
                        }
                    }
                }

            TOP ->
                for (row in field.indices) {
                    for (column in field[row].indices) {
                        if (row == field.size - 1 || field[row][column] == 0) {
                            continue
                        } else if (field[row + 1][column] == field[row][column]) {
                            score += field[row][column] * 2
                            field[row][column] = field[row][column] * 2
                            field[row + 1][column] = 0
                        }
                    }
                }

            LEFT ->
                for (row in field.indices) {
                    for (column in field.indices) {
                        if (column == field[row].size - 1 || field[row][column] == 0) {
                            continue
                        } else if (field[row][column + 1] == field[row][column]) {
                            score += field[row][column] * 2
                            field[row][column] = field[row][column] * 2
                            field[row][column + 1] = 0
                        }
                    }
                }

            RIGHT ->
                for (row in field.indices) {
                    for (column in field.size - 1 downTo 1) {
                        if (field[row][column] == 0) {
                            continue
                        } else if (field[row][column - 1] == field[row][column]) {
                            score += field[row][column] * 2
                            field[row][column] = field[row][column] * 2
                            field[row][column - 1] = 0
                        }
                    }
                }

            else -> return game
        }

        return game.copy(field = field, score = score)
    }

    private fun checkAddition(game: Game, direction: Direction): Boolean {
        when (direction) {
            BOTTOM ->
                for (row in game.field.size - 1 downTo 1) {
                    for (column in game.field[row].indices) {
                        if (game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row - 1][column] == game.field[row][column]) {
                            return true
                        }
                    }
                }

            TOP ->
                for (row in game.field.indices) {
                    for (column in game.field[row].indices) {
                        if (row == game.field.size - 1 || game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row + 1][column] == game.field[row][column]) {
                            return true
                        }
                    }
                }

            LEFT ->
                for (row in game.field.indices) {
                    for (column in game.field.indices) {
                        if (column == game.field[row].size - 1 || game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row][column + 1] == game.field[row][column]) {
                            return true
                        }
                    }
                }

            RIGHT ->
                for (row in game.field.indices) {
                    for (column in game.field.size - 1 downTo 1) {
                        if (game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row][column - 1] == game.field[row][column]) {
                            return true
                        }
                    }
                }

            else -> return false
        }

        return false
    }
}

fun main() {
    val list = buildList {
        repeat(4) {
            add(buildList { repeat(4) { add(0) } }.toMutableList())
        }
    }.toMutableList()
    list[0][0] = 2
    list[0][1] = 64
    list[0][2] = 4
    list[0][3] = 2
    list[1][0] = 4
    list[1][1] = 128
    list[1][2] = 16
    list[1][3] = 4
    list[2][0] = 16
    list[2][1] = 64
    list[2][2] = 32
    list[2][3] = 2
    list[3][0] = 2
    list[3][1] = 8
    list[3][2] = 16
    list[3][3] = 4

    val rep = GameRepositoryImpl()
    val game = rep.startGame(4, 4, list)
    val game2 = rep.swipeFieldToDirection(game, BOTTOM, true)
    println(game)
    println(game2)
    println(game == game2)
}