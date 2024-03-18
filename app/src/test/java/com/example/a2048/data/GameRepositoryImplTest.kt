package com.example.a2048.data

import com.example.a2048.util.Direction
import com.example.a2048.domain.entity.Game
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(value = Parameterized::class)
class GameRepositoryImplTest(
    private val startingField: List<List<Int>>,
    private val direction: Direction,
    private val expectedField: List<List<Int>>,
    private val expectedScore: Int
) {
    private val repositoryImpl = GameRepositoryImpl()
    private lateinit var game: Game

    @Before
    fun setup() {
        game = repositoryImpl.startGame(4, 4, startingField)
    }

    @Test
    fun isFieldMoved() {
       val result =  repositoryImpl.swipeFieldToDirection(game, direction, true)
        Assert.assertEquals(expectedField, result.field)
    }

    @Test
    fun isScoreCalculate() {
       val result = repositoryImpl.swipeFieldToDirection(game, direction, true)
        Assert.assertEquals(expectedScore, result.score)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: isValid({0})={1}")
        fun data(): Iterable<Array<Any>> {

            return arrayListOf(
                arrayOf(
                    listOf(
                        listOf(0, 0, 0, 2),
                        listOf(0, 0, 2, 2),
                        listOf(0, 2, 2, 2),
                        listOf(2, 2, 2, 2)
                    ),
                    Direction.TOP,
                    listOf(
                        listOf(2, 4, 4, 4),
                        listOf(0, 0, 2, 4),
                        listOf(0, 0, 0, 0),
                        listOf(0, 0, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(2, 2, 2, 2),
                        listOf(2, 2, 2, 0),
                        listOf(2, 2, 0, 0),
                        listOf(2, 0, 0, 0)
                    ),
                    Direction.TOP,
                    listOf(
                        listOf(4, 4, 4, 2),
                        listOf(4, 2, 0, 0),
                        listOf(0, 0, 0, 0),
                        listOf(0, 0, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(2, 2, 2, 2),
                        listOf(0, 2, 2, 2),
                        listOf(0, 0, 2, 2),
                        listOf(0, 0, 0, 2)
                    ),
                    Direction.LEFT,
                    listOf(
                        listOf(4, 4, 0, 0),
                        listOf(4, 2, 0, 0),
                        listOf(4, 0, 0, 0),
                        listOf(2, 0, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(2, 0, 0, 0),
                        listOf(2, 2, 0, 0),
                        listOf(2, 2, 2, 0),
                        listOf(2, 2, 2, 2)
                    ),
                    Direction.LEFT,
                    listOf(
                        listOf(2, 0, 0, 0),
                        listOf(4, 0, 0, 0),
                        listOf(4, 2, 0, 0),
                        listOf(4, 4, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(2, 2, 2, 2),
                        listOf(2, 2, 2, 0),
                        listOf(2, 2, 0, 0),
                        listOf(2, 0, 0, 0)
                    ),
                    Direction.RIGHT,
                    listOf(
                        listOf(0, 0, 4, 4),
                        listOf(0, 0, 2, 4),
                        listOf(0, 0, 0, 4),
                        listOf(0, 0, 0, 2)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(0, 0, 0, 2),
                        listOf(0, 0, 2, 2),
                        listOf(0, 2, 2, 2),
                        listOf(2, 2, 2, 2)
                    ),
                    Direction.RIGHT,
                    listOf(
                        listOf(0, 0, 0, 2),
                        listOf(0, 0, 0, 4),
                        listOf(0, 0, 2, 4),
                        listOf(0, 0, 4, 4)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(2, 2, 2, 2),
                        listOf(2, 2, 2, 0),
                        listOf(2, 2, 0, 0),
                        listOf(2, 0, 0, 0)
                    ),
                    Direction.BOTTOM,
                    listOf(
                        listOf(0, 0, 0, 0),
                        listOf(0, 0, 0, 0),
                        listOf(4, 2, 0, 0),
                        listOf(4, 4, 4, 2)
                    ),
                    16
                ),
                arrayOf(
                    listOf(
                        listOf(0, 0, 0, 2),
                        listOf(0, 0, 2, 2),
                        listOf(0, 2, 2, 2),
                        listOf(2, 2, 2, 2)
                    ),
                    Direction.BOTTOM,
                    listOf(
                        listOf(0, 0, 0, 0),
                        listOf(0, 0, 0, 0),
                        listOf(0, 0, 2, 4),
                        listOf(2, 4, 4, 4)
                    ),
                    16
                )
            )
        }
    }
}