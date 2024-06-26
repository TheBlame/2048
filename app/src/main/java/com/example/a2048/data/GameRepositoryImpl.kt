package com.example.a2048.data

import androidx.datastore.core.DataStore
import com.example.a2048.data.database.AppDatabase
import com.example.a2048.data.datastore.SavedGames
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.entity.GameScore
import com.example.a2048.domain.repository.GameRepository
import com.example.a2048.util.CellCoordinates
import com.example.a2048.util.Direction
import com.example.a2048.util.Direction.BOTTOM
import com.example.a2048.util.Direction.LEFT
import com.example.a2048.util.Direction.RIGHT
import com.example.a2048.util.Direction.TOP
import com.example.a2048.util.Helpers.Companion.buildEmptyField
import com.example.a2048.util.Helpers.Companion.mapGameAndDateToScoreDbModel
import com.example.a2048.util.Helpers.Companion.mapScoreDbModelToGameScore
import com.example.a2048.util.Helpers.Companion.twoDimensionalListToMutableList
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val ds: DataStore<SavedGames>
) : GameRepository {

    override suspend fun startGame(gameMode: GameMode, startingField: List<List<Int>>?): Game {
        var game: Game

        if (startingField != null) {
            game = Game(gameMode, startingField, 0)
        } else {
            val list = buildEmptyField(gameMode)

            val topScore = db.dbDao().getTopScoreByMode(gameMode) ?: 0

            game = Game(gameMode, list, topScore)
            repeat(2) { game = addNumberToField(game) }
        }

        val possibleMoves = checkPossibleMoves(game)

        with(game.copy(possibleDirections = possibleMoves)) {
            saveGameToDataStore(this)
            return this
        }
    }

    override suspend fun continueGame(gameMode: GameMode): Game? {
        return ds.data.first().games[gameMode]
    }

    override suspend fun swipeFieldToDirection(game: Game, direction: Direction, testMode: Boolean): Game {
        if (!game.possibleDirections.contains(direction)) return game

        var newGameState = moveFieldWithAddition(game, direction)

        if (!testMode) {
            newGameState = addNumberToField(newGameState)
        }

        val possibleMoves = checkPossibleMoves(newGameState)

        if (possibleMoves.isEmpty()) {
            removeGameFromDataStore(game.gameMode)

            return newGameState.copy(
                possibleDirections = possibleMoves,
                gameOver = true
            )
        }

        with(newGameState.copy(possibleDirections = possibleMoves)) {
            saveGameToDataStore(this)
            return this
        }
    }

    override suspend fun saveScore(game: Game) {
        val date = SimpleDateFormat("dd.MM.yyyy, h:mm", Locale.getDefault())
        val dateFormat: String = date.format(Calendar.getInstance().time)
        db.dbDao().saveScore(mapGameAndDateToScoreDbModel(game, dateFormat))
    }

    override suspend fun getScoresByMode(gameMode: GameMode): List<GameScore> {
        return buildList {
            db.dbDao().getScoresByMode(gameMode).iterator().forEachRemaining {
                this.add(mapScoreDbModelToGameScore(it))
            }
        }
    }

    private suspend fun saveGameToDataStore(game: Game) {
        ds.updateData {savedGames ->
            savedGames.copy(
                games = savedGames.games.mutate {
                    it[game.gameMode] = game
                }
            )
        }
    }

    private suspend fun removeGameFromDataStore(gameMode: GameMode) {
        ds.updateData { savedGames ->
            savedGames.copy(
                games = savedGames.games.mutate {
                    it.remove(gameMode)
                }
            )
        }
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
                    for (column in field[row].indices) {
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
                    for (column in field[row].size - 1 downTo 1) {
                        if (field[row][column] == 0) {
                            continue
                        } else if (field[row][column - 1] == field[row][column]) {
                            score += field[row][column] * 2
                            field[row][column] = field[row][column] * 2
                            field[row][column - 1] = 0
                        }
                    }
                }
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
                    for (column in game.field[row].indices) {
                        if (column == game.field[row].size - 1 || game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row][column + 1] == game.field[row][column]) {
                            return true
                        }
                    }
                }

            RIGHT ->
                for (row in game.field.indices) {
                    for (column in game.field[row].size - 1 downTo 1) {
                        if (game.field[row][column] == 0) {
                            continue
                        } else if (game.field[row][column - 1] == game.field[row][column]) {
                            return true
                        }
                    }
                }
        }

        return false
    }
}