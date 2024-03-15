package com.example.a2048.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameSetting(
    val rows: Int,
    val columns: Int
) : Parcelable