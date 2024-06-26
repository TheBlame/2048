package com.example.a2048.presentation.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.a2048.R
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.util.CellCoordinates
import com.example.a2048.util.Direction
import com.example.a2048.util.Helpers.Companion.color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class GameFieldView(
    context: Context,
    attributesSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attributesSet, defStyleAttr, defStyleRes) {

    var game: Game? = null
        set(value) {
            if (value != null && !value.field.contains(emptyList())) {
                changedCells.clear()
                value.lastAddedCell?.let { changedCells.add(it) }
                checkChangedCells(game?.field, value.field)
                field = value
                rows = value.gameMode.getGameSetting().rows
                columns = value.gameMode.getGameSetting().columns
                updateViewSizes()
                requestLayout()
                invalidate()
                if (cellSize != 0f) {
                    startAnimation()
                }
            }
        }

    private var rows = 0
    private var columns = 0

    private val changedCells = mutableSetOf<CellCoordinates>()

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null) {
                val angle = getAngle(e1.x, e1.y, e2.x, e2.y)
                val direction = Direction[angle]
                swipeListener?.invoke(direction)
            }
            return true
        }
    })

    private var swipeListener: ((Direction) -> Unit)? = null

    fun setSwipeListener(o: ((Direction) -> Unit)?) {
        swipeListener = o
    }

    private var fieldColor by Delegates.notNull<Int>()
    private var cellEmptyColor by Delegates.notNull<Int>()
    private var cornerRadius by Delegates.notNull<Float>()
    private var animationDuration by Delegates.notNull<Int>()

    private val fieldRect = RectF()
    private val cellRect = RectF()
    private val cellPath = Path()
    private val textBounds = Rect()
    private var cellSize = 0f
    private var cellPadding = 0f
    private var textX = 0f
    private var textY = 0f
    private var textLength = 0f
    private var textHeight = 0
    private var textSize = 0f

    private lateinit var fieldPaint: Paint
    private lateinit var cell2Paint: Paint
    private lateinit var cell4Paint: Paint
    private lateinit var cell8Paint: Paint
    private lateinit var cell16Paint: Paint
    private lateinit var cell32Paint: Paint
    private lateinit var cell64Paint: Paint
    private lateinit var cell128Paint: Paint
    private lateinit var cell256Paint: Paint
    private lateinit var cell512Paint: Paint
    private lateinit var cell1024Paint: Paint
    private lateinit var cell2048Paint: Paint
    private lateinit var cell4096Paint: Paint
    private lateinit var cell8192Paint: Paint
    private lateinit var cell16384Paint: Paint
    private lateinit var cell32768Paint: Paint
    private lateinit var cell65536Paint: Paint
    private lateinit var cellBigNumberPaint: Paint
    private lateinit var cellEmptyPaint: Paint
    private lateinit var lightBackgroundTextPaint: Paint
    private lateinit var darkBackgroundTextPaint: Paint

    private lateinit var cellAnimator: ValueAnimator
    private lateinit var textAnimator: ValueAnimator

    private var cellAnimationValue = 0f
    private var textAnimationValue = 0f

    private var currentCellAnimation = 0f
    private var currentTextAnimation = 0f

    constructor(context: Context, attributesSet: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attributesSet,
        defStyleAttr,
        R.style.DefaultGameFieldStyle
    )

    constructor(context: Context, attributesSet: AttributeSet?) : this(
        context,
        attributesSet,
        R.attr.gameFieldStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        if (attributesSet != null) {
            initAttributes(attributesSet, defStyleAttr, defStyleRes)
        } else {
            initDefaultAttr()
        }
        initPaints()
        if (isInEditMode) {
            val list = buildList {
                repeat(9) {
                    add(buildList { repeat(6) { add(0) } }.toMutableList())
                }
            }.toMutableList()
            list[6][5] = 8
            list[7][4] = 4
            list[8][3] = 2
            list[8][4] = 16
            list[7][5] = 32
            list[8][5] = 128
            list[8][2] = 4096
            list[7][2] = 8192
            list[6][2] = 16384
            list[5][2] = 32768
            list[4][2] = 65536

            game = Game(GameMode.MODE6x9, list, 0)
        }
    }

    private fun initAttributes(
        attributesSet: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        val typedArray = context.obtainStyledAttributes(
            attributesSet,
            R.styleable.GameFieldView,
            defStyleAttr,
            defStyleRes
        )
        cellEmptyColor = typedArray.getColor(
            R.styleable.GameFieldView_emptyCellColor,
            context.color(R.color.cell_empty)
        )
        fieldColor =
            typedArray.getColor(R.styleable.GameFieldView_gridColor, context.color(R.color.field))
        cornerRadius =
            typedArray.getDimension(R.styleable.GameFieldView_cornerRadius, DEFAULT_FIELD_CORNER)
        animationDuration = typedArray.getInt(
            R.styleable.GameFieldView_animationDuration,
            DEFAULT_ANIMATION_DURATION
        )
        typedArray.recycle()
    }

    private fun initDefaultAttr() {
        cellEmptyColor = context.color(R.color.cell_empty)
        fieldColor = context.color(R.color.field)
        cornerRadius = DEFAULT_FIELD_CORNER
        animationDuration = DEFAULT_ANIMATION_DURATION
    }

    private fun initPaints() {
        fieldPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fieldPaint.color = fieldColor
        fieldPaint.style = Paint.Style.FILL

        cell2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell2Paint.color = context.color(R.color.cell_value_2)
        cell2Paint.style = Paint.Style.FILL

        cell4Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell4Paint.color = context.color(R.color.cell_value_4)
        cell4Paint.style = Paint.Style.FILL

        cell8Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell8Paint.color = context.color(R.color.cell_value_8)
        cell8Paint.style = Paint.Style.FILL

        cell16Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell16Paint.color = context.color(R.color.cell_value_16)
        cell16Paint.style = Paint.Style.FILL

        cell32Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell32Paint.color = context.color(R.color.cell_value_32)
        cell32Paint.style = Paint.Style.FILL

        cell64Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell64Paint.color = context.color(R.color.cell_value_64)
        cell64Paint.style = Paint.Style.FILL

        cell128Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell128Paint.color = context.color(R.color.cell_value_128)
        cell128Paint.style = Paint.Style.FILL

        cell256Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell256Paint.color = context.color(R.color.cell_value_256)
        cell256Paint.style = Paint.Style.FILL

        cell512Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell512Paint.color = context.color(R.color.cell_value_512)
        cell512Paint.style = Paint.Style.FILL

        cell1024Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell1024Paint.color = context.color(R.color.cell_value_1024)
        cell1024Paint.style = Paint.Style.FILL

        cell2048Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell2048Paint.color = context.color(R.color.cell_value_2048)
        cell2048Paint.style = Paint.Style.FILL

        cell4096Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell4096Paint.color = context.color(R.color.cell_value_4096)
        cell4096Paint.style = Paint.Style.FILL

        cell8192Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell8192Paint.color = context.color(R.color.cell_value_8192)
        cell8192Paint.style = Paint.Style.FILL

        cell16384Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell16384Paint.color = context.color(R.color.cell_value_16384)
        cell16384Paint.style = Paint.Style.FILL

        cell32768Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell32768Paint.color = context.color(R.color.cell_value_32768)
        cell32768Paint.style = Paint.Style.FILL

        cell65536Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell65536Paint.color = context.color(R.color.cell_value_65536)
        cell65536Paint.style = Paint.Style.FILL

        cellBigNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        cellBigNumberPaint.color = Color.BLACK
        cellBigNumberPaint.style = Paint.Style.FILL

        cellEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        cellEmptyPaint.color = cellEmptyColor
        cellEmptyPaint.style = Paint.Style.FILL

        lightBackgroundTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        lightBackgroundTextPaint.color = context.color(R.color.gray)

        darkBackgroundTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        darkBackgroundTextPaint.color = context.color(R.color.white)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSizes()
        startAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        if (game == null) {
            setMeasuredDimension(
                resolveSize(minWidth, widthMeasureSpec),
                resolveSize(minHeight, heightMeasureSpec)
            )
            return
        }

        val screenWidth =
            MeasureSpec.getSize(widthMeasureSpec) - marginStart - marginEnd - paddingLeft - paddingRight

        val screenHeight =
            MeasureSpec.getSize(heightMeasureSpec) - marginTop - marginBottom - paddingTop - paddingBottom

        val desiredCellWidthInPixels = screenWidth / columns

        val desiredCellHeightInPixels = screenHeight / rows

        val desiredCellSizeInPixels = min(desiredCellHeightInPixels, desiredCellWidthInPixels)

        val desiredWidth =
            max(minWidth, columns * desiredCellSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight =
            max(minHeight, rows * desiredCellSizeInPixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (game == null) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawField(canvas)
        drawCells(canvas)
    }

    private fun drawField(canvas: Canvas) {
        canvas.drawRoundRect(fieldRect, cornerRadius, cornerRadius, fieldPaint)
    }

    private fun drawCells(canvas: Canvas) {
        val field = this.game?.field ?: return

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                drawCell(canvas, row, column, field[row][column])
            }
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, column: Int, cellValue: Int) {
        currentCellAnimation =
            if (changedCells.contains(CellCoordinates(row, column)) && cellValue != 0)
                cellAnimationValue / 2 else 0f

        currentTextAnimation =
            if (changedCells.contains(CellCoordinates(row, column)) && cellValue != 0)
                textAnimationValue else 0f

        cellRect.left = fieldRect.left + column * cellSize + currentCellAnimation + cellPadding
        cellRect.top = fieldRect.top + row * cellSize + currentCellAnimation + cellPadding
        cellRect.right = cellRect.left + cellSize - currentCellAnimation * 2 - cellPadding
        cellRect.bottom = cellRect.top + cellSize - currentCellAnimation * 2 - cellPadding

        cellPath.addRoundRect(cellRect, cornerRadius, cornerRadius, Path.Direction.CW)

        darkBackgroundTextPaint.textSize = textSize - currentTextAnimation

        darkBackgroundTextPaint.getTextBounds(
            cellValue.toString(),
            0,
            cellValue.toString().length,
            textBounds
        )

        while (darkBackgroundTextPaint.measureText(cellValue.toString()) > cellSize - cellPadding * 2 || textBounds.height() > cellSize - cellPadding * 2) {
            darkBackgroundTextPaint.textSize--
            darkBackgroundTextPaint.getTextBounds(
                cellValue.toString(),
                0,
                cellValue.toString().length,
                textBounds
            )
        }

        textLength = darkBackgroundTextPaint.measureText(cellValue.toString())

        textHeight = textBounds.height()

        textX =
            (cellRect.left + cellSize / 2 - textLength / 2 - cellPadding / 2) - currentCellAnimation
        textY =
            (cellRect.top + cellSize / 2 + textHeight / 2 - cellPadding / 2) - currentCellAnimation

        if (cellValue == 2 || cellValue == 4) lightBackgroundTextPaint.textSize =
            darkBackgroundTextPaint.textSize

        when (cellValue) {
            0 -> canvas.drawPath(cellPath, cellEmptyPaint)

            2 -> canvas.drawPathWithText(
                cellPath,
                cell2Paint,
                cellValue.toString(),
                textX,
                textY,
                lightBackgroundTextPaint
            )

            4 -> canvas.drawPathWithText(
                cellPath,
                cell4Paint,
                cellValue.toString(),
                textX,
                textY,
                lightBackgroundTextPaint
            )

            8 -> canvas.drawPathWithText(
                cellPath,
                cell8Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            16 -> canvas.drawPathWithText(
                cellPath,
                cell16Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            32 -> canvas.drawPathWithText(
                cellPath,
                cell32Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            64 -> canvas.drawPathWithText(
                cellPath,
                cell64Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            128 -> canvas.drawPathWithText(
                cellPath,
                cell128Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            256 -> canvas.drawPathWithText(
                cellPath,
                cell256Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            512 -> canvas.drawPathWithText(
                cellPath,
                cell512Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            1024 -> canvas.drawPathWithText(
                cellPath,
                cell1024Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            2048 -> canvas.drawPathWithText(
                cellPath,
                cell2048Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            4096 -> canvas.drawPathWithText(
                cellPath,
                cell4096Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            8192 -> canvas.drawPathWithText(
                cellPath,
                cell8192Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            16384 -> canvas.drawPathWithText(
                cellPath,
                cell16384Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            32768 -> canvas.drawPathWithText(
                cellPath,
                cell32768Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            65536 -> canvas.drawPathWithText(
                cellPath,
                cell65536Paint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )

            else -> canvas.drawPathWithText(
                cellPath,
                cellBigNumberPaint,
                cellValue.toString(),
                textX,
                textY,
                darkBackgroundTextPaint
            )
        }
        cellPath.reset()
    }

    private fun updateViewSizes() {
        if (game == null) return

        val fieldWidth = width - paddingLeft - paddingRight
        val fieldHeight = height - paddingTop - paddingBottom

        val cellWidth = fieldWidth / columns.toFloat()
        val cellHeight = fieldHeight / rows.toFloat()

        cellPadding = min(cellWidth, cellHeight) * 0.08f

        cellSize = min(cellWidth, cellHeight) - cellPadding / columns

        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            cellSize * 0.16f,
            resources.displayMetrics
        )

        fieldRect.left = paddingLeft.toFloat()
        fieldRect.top = paddingTop.toFloat()
        fieldRect.right = fieldRect.left + cellSize * columns + cellPadding + paddingRight
        fieldRect.bottom = fieldRect.top + cellSize * rows + cellPadding + paddingBottom
    }

    private fun getAngle(startX: Float, startY: Float, endX: Float, endY: Float): Float {
        val a = endX - startX
        val b = endY - startY
        val radian = atan2(b, a)

        return ((180f / PI.toFloat() * radian) + 360) % 360
    }

    private fun startAnimation() {
        textAnimator = ValueAnimator.ofFloat(0f, textSize * 0.7f).apply {
            duration = animationDuration.toLong()
            reverse()
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                textAnimationValue = valueAnimator.animatedValue as Float
            }
        }

        cellAnimator = ValueAnimator.ofFloat(0f, cellSize * 0.7f).apply {
            duration = animationDuration.toLong()
            reverse()
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                cellAnimationValue = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
    }

    private fun checkChangedCells(oldList: List<List<Int>>?, newList: List<List<Int>>) {
        if (oldList == null) {
            for (row in newList.indices) {
                for (column in newList[row].indices) {
                    if (newList[row][column] != 0) {
                        changedCells.add(CellCoordinates(row, column))
                    }
                }
            }
        } else {
            for (row in oldList.indices) {
                for (column in oldList[row].indices) {
                    if (oldList[row][column] != newList[row][column]) {
                        changedCells.add(CellCoordinates(row, column))
                    }
                }
            }
        }
    }

    private fun Canvas.drawPathWithText(
        path: Path,
        pathPaint: Paint,
        text: String,
        textX: Float,
        textY: Float,
        textPaint: Paint
    ) {
        drawPath(path, pathPaint)
        drawText(text, textX, textY, textPaint)
    }

    companion object {
        const val DEFAULT_FIELD_CORNER = 0f
        const val DEFAULT_ANIMATION_DURATION = 0
    }
}


