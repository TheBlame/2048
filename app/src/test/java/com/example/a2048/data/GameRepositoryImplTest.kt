package com.example.a2048.data

import com.example.a2048.Direction
import com.example.a2048.domain.entity.Game
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized


@RunWith(value = Parameterized::class)
class GameRepositoryImplTest(
    private val startingField: Array<IntArray>,
    private val direction: Direction,
    private val expectedField: Array<IntArray>,
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
        repositoryImpl.swipeFieldToDirection(direction, true)
        Assert.assertArrayEquals(expectedField, game.field)
    }

    @Test
    fun isScoreCalculate() {
        repositoryImpl.swipeFieldToDirection(direction, true)
        Assert.assertEquals(expectedScore, game.score)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: isValid({0})={1}")
        fun data(): Iterable<Array<Any>> {

            return arrayListOf(
                arrayOf(
                    arrayOf(
                        intArrayOf(0, 0, 0, 2),
                        intArrayOf(0, 0, 2, 2),
                        intArrayOf(0, 2, 2, 2),
                        intArrayOf(2, 2, 2, 2)
                    ),
                    Direction.TOP,
                    arrayOf(
                        intArrayOf(2, 4, 4, 4),
                        intArrayOf(0, 0, 2, 4),
                        intArrayOf(0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(2, 2, 2, 2),
                        intArrayOf(2, 2, 2, 0),
                        intArrayOf(2, 2, 0, 0),
                        intArrayOf(2, 0, 0, 0)
                    ),
                    Direction.TOP,
                    arrayOf(
                        intArrayOf(4, 4, 4, 2),
                        intArrayOf(4, 2, 0, 0),
                        intArrayOf(0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(2, 2, 2, 2),
                        intArrayOf(0, 2, 2, 2),
                        intArrayOf(0, 0, 2, 2),
                        intArrayOf(0, 0, 0, 2)
                    ),
                    Direction.LEFT,
                    arrayOf(
                        intArrayOf(4, 4, 0, 0),
                        intArrayOf(4, 2, 0, 0),
                        intArrayOf(4, 0, 0, 0),
                        intArrayOf(2, 0, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(2, 0, 0, 0),
                        intArrayOf(2, 2, 0, 0),
                        intArrayOf(2, 2, 2, 0),
                        intArrayOf(2, 2, 2, 2)
                    ),
                    Direction.LEFT,
                    arrayOf(
                        intArrayOf(2, 0, 0, 0),
                        intArrayOf(4, 0, 0, 0),
                        intArrayOf(4, 2, 0, 0),
                        intArrayOf(4, 4, 0, 0)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(2, 2, 2, 2),
                        intArrayOf(2, 2, 2, 0),
                        intArrayOf(2, 2, 0, 0),
                        intArrayOf(2, 0, 0, 0)
                    ),
                    Direction.RIGHT,
                    arrayOf(
                        intArrayOf(0, 0, 4, 4),
                        intArrayOf(0, 0, 2, 4),
                        intArrayOf(0, 0, 0, 4),
                        intArrayOf(0, 0, 0, 2)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(0, 0, 0, 2),
                        intArrayOf(0, 0, 2, 2),
                        intArrayOf(0, 2, 2, 2),
                        intArrayOf(2, 2, 2, 2)
                    ),
                    Direction.RIGHT,
                    arrayOf(
                        intArrayOf(0, 0, 0, 2),
                        intArrayOf(0, 0, 0, 4),
                        intArrayOf(0, 0, 2, 4),
                        intArrayOf(0, 0, 4, 4)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(2, 2, 2, 2),
                        intArrayOf(2, 2, 2, 0),
                        intArrayOf(2, 2, 0, 0),
                        intArrayOf(2, 0, 0, 0)
                    ),
                    Direction.BOTTOM,
                    arrayOf(
                        intArrayOf(0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0),
                        intArrayOf(4, 2, 0, 0),
                        intArrayOf(4, 4, 4, 2)
                    ),
                    16
                ),
                arrayOf(
                    arrayOf(
                        intArrayOf(0, 0, 0, 2),
                        intArrayOf(0, 0, 2, 2),
                        intArrayOf(0, 2, 2, 2),
                        intArrayOf(2, 2, 2, 2)
                    ),
                    Direction.BOTTOM,
                    arrayOf(
                        intArrayOf(0, 0, 0, 0),
                        intArrayOf(0, 0, 0, 0),
                        intArrayOf(0, 0, 2, 4),
                        intArrayOf(2, 4, 4, 4)
                    ),
                    16
                )
            )
        }
    }
}