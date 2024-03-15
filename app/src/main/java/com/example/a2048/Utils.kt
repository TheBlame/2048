package com.example.a2048

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

class Utils {
    companion object {
        fun Array<IntArray>.deepCopy() = Array(size) { get(it).clone() }

        @ColorInt
        fun Context.color(@ColorRes colorResId: Int) =
            ContextCompat.getColor(this, colorResId)

        data class CellCoordinates(
            val row: Int,
            val column: Int
        )
    }
}