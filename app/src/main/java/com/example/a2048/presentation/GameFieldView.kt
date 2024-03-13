package com.example.a2048.presentation

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
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.a2048.Direction
import com.example.a2048.R
import com.example.a2048.data.GameRepositoryImpl
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

    var gameField: GameField? = null
        set(value) {
            field = value
            updateViewSizes()
            requestLayout()
            invalidate()
            if (cellSize != 0f) {
                startAnimation()
            }
        }

    private var swipeListener: ((Direction) -> Unit)? = null

    fun setSwipeListener(o: ((Direction) -> Unit)?) {
        swipeListener = o
    }

    private var gridColor by Delegates.notNull<Int>()
    private var cellColor by Delegates.notNull<Int>()
    private lateinit var cellText: String

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

    private var touchStartX = 0f
    private var touchStartY = 0f
    private var touchEndX = 0f
    private var touchEndY = 0f

    private lateinit var gridPaint: Paint
    private lateinit var cellEmptyPaint: Paint
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
    private lateinit var cellBigNumberPaint: Paint
    private lateinit var cellTextPaint: Paint
    private val textSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        TEXT_SIZE,
        resources.displayMetrics
    )

    private lateinit var cellAnimator: ValueAnimator
    private lateinit var textAnimator: ValueAnimator

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
            val rep = GameRepositoryImpl()
            gameField = GameField(rep.startGame(4, 4).field)
            gameField!!.field[0][0] = 16
            gameField!!.field[3][1] = 128
        }
    }

    private fun startAnimation() {
        textAnimator = ValueAnimator.ofFloat(0f, textSize * 0.3f).apply {
            duration = ANIMATION_DURATION
            reverse()
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentTextAnimation = valueAnimator.animatedValue as Float
            }
        }

        cellAnimator = ValueAnimator.ofFloat(0f, cellSize * 0.3f).apply {
            duration = ANIMATION_DURATION
            reverse()
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                currentCellAnimation = valueAnimator.animatedValue as Float
                invalidate()
            }
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
        gridColor = typedArray.getColor(R.styleable.GameFieldView_gridColor, GRID_DEFAULT_COLOR)
        cellColor = typedArray.getColor(R.styleable.GameFieldView_cellColor, CELL_EMPTY_COLOR)
        cellText = typedArray.getString(R.styleable.GameFieldView_cellText).toString()
        typedArray.recycle()
    }

    private fun initPaints() {
        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                GRID_STROKE_WIDTH,
                resources.displayMetrics
            )

        cellEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        cellEmptyPaint.color = CELL_EMPTY_COLOR
        cellEmptyPaint.style = Paint.Style.FILL

        cell2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell2Paint.color = context.color(R.color.cellValue2)
        cell2Paint.style = Paint.Style.FILL

        cell4Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell4Paint.color = context.color(R.color.cellValue4)
        cell4Paint.style = Paint.Style.FILL

        cell8Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell8Paint.color = context.color(R.color.cellValue8)
        cell8Paint.style = Paint.Style.FILL

        cell16Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell16Paint.color = context.color(R.color.cellValue16)
        cell16Paint.style = Paint.Style.FILL

        cell32Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell32Paint.color = context.color(R.color.cellValue32)
        cell32Paint.style = Paint.Style.FILL

        cell64Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell64Paint.color = context.color(R.color.cellValue64)
        cell64Paint.style = Paint.Style.FILL

        cell128Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell128Paint.color = context.color(R.color.cellValue128)
        cell128Paint.style = Paint.Style.FILL

        cell256Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell256Paint.color = context.color(R.color.cellValue256)
        cell256Paint.style = Paint.Style.FILL

        cell512Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell512Paint.color = context.color(R.color.cellValue512)
        cell512Paint.style = Paint.Style.FILL

        cell1024Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell1024Paint.color = context.color(R.color.cellValue1024)
        cell1024Paint.style = Paint.Style.FILL

        cell2048Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        cell2048Paint.color = context.color(R.color.cellValue2048)
        cell2048Paint.style = Paint.Style.FILL

        cellBigNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        cellBigNumberPaint.color = Color.BLACK
        cellBigNumberPaint.style = Paint.Style.FILL

        cellTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        cellTextPaint.color = Color.WHITE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateViewSizes()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DESIRED_CELL_SIZE,
            resources.displayMetrics
        ).toInt()
        val rows = gameField?.rows ?: 0
        val columns = gameField?.columns ?: 0

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
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                touchEndX = event.x
                touchEndY = event.y
            }

            MotionEvent.ACTION_UP -> {
                val angle = getAngle(touchStartX, touchStartY, touchEndX, touchEndY)
                swipeListener?.invoke(Direction[angle])
                return true
            }
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (gameField == null) return
        if (cellSize == 0f) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawGrid(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val field = this.gameField ?: return

        val xStart = fieldRect.left
        val xEnd = fieldRect.right
        for (i in 0..field.rows) {
            val y = fieldRect.top + cellSize * i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top
        val yEnd = fieldRect.bottom
        for (i in 0..field.columns) {
            val x = fieldRect.left + cellSize * i
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }
    }

    private fun drawCells(canvas: Canvas) {
        val field = this.gameField ?: return

        for (row in 0 until field.rows) {
            for (column in 0 until field.columns) {
                drawCell(canvas, row, column, field.getCell(row, column))
            }
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, column: Int, cellValue: Int) {
        cellRect.left = (fieldRect.left + column * cellSize + cellPadding) + currentCellAnimation
        cellRect.top = (fieldRect.top + row * cellSize + cellPadding) + currentCellAnimation
        cellRect.right = (cellRect.left + cellSize - cellPadding * 2) - currentCellAnimation * 2
        cellRect.bottom = (cellRect.top + cellSize - cellPadding * 2) - currentCellAnimation * 2

        cellPath.addRect(cellRect, Path.Direction.CW)

        textLength = cellTextPaint.measureText(cellValue.toString())

        cellTextPaint.getTextBounds(
            cellValue.toString(),
            0,
            cellValue.toString().length,
            textBounds
        )
        textHeight = textBounds.height()

        textX = (cellRect.left + cellSize / 2 - textLength / 2 - cellPadding) - currentCellAnimation
        textY = (cellRect.top + cellSize / 2 + textHeight / 2 - cellPadding) - currentCellAnimation

        cellTextPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            TEXT_SIZE,
            resources.displayMetrics
        ) - currentTextAnimation

        when (cellValue) {
            0 -> canvas.drawPath(cellPath, cellEmptyPaint)

            2 -> canvas.drawPathWithText(
                cellPath,
                cell2Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            4 -> canvas.drawPathWithText(
                cellPath,
                cell4Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            8 -> canvas.drawPathWithText(
                cellPath,
                cell8Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            16 -> canvas.drawPathWithText(
                cellPath,
                cell16Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            32 -> canvas.drawPathWithText(
                cellPath,
                cell32Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            64 -> canvas.drawPathWithText(
                cellPath,
                cell64Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            128 -> canvas.drawPathWithText(
                cellPath,
                cell128Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            256 -> canvas.drawPathWithText(
                cellPath,
                cell256Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            512 -> canvas.drawPathWithText(
                cellPath,
                cell512Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            1024 -> canvas.drawPathWithText(
                cellPath,
                cell1024Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            2048 -> canvas.drawPathWithText(
                cellPath,
                cell2048Paint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )

            else -> canvas.drawPathWithText(
                cellPath,
                cellBigNumberPaint,
                cellValue.toString(),
                textX,
                textY,
                cellTextPaint
            )
        }
        cellPath.reset()
    }

    private fun updateViewSizes() {
        val field = this.gameField ?: return

        val safeWidth = width - paddingLeft - paddingRight
        val safeHeight = height - paddingTop - paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.035f

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    private fun initDefaultAttr() {
        gridColor = GRID_DEFAULT_COLOR
        cellColor = CELL_EMPTY_COLOR
        cellText = CELL_DEFAULT_TEXT
    }

    private fun getAngle(startX: Float, startY: Float, endX: Float, endY: Float): Float {
        val a = endX - startX
        val b = endY - startY
        val radian = atan2(b, a)

        return ((180f / PI.toFloat() * radian) + 360) % 360
    }

    @ColorInt
    fun Context.color(@ColorRes colorResId: Int) =
        ContextCompat.getColor(this, colorResId)

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
        const val GRID_DEFAULT_COLOR = Color.GRAY
        const val CELL_EMPTY_COLOR = Color.TRANSPARENT
        const val CELL_DEFAULT_TEXT = ""
        const val GRID_STROKE_WIDTH = 10f
        const val TEXT_SIZE = 50f
        const val ANIMATION_DURATION = 75L
        const val DESIRED_CELL_SIZE = 50f
    }
}


