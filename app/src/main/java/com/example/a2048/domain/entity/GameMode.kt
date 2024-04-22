package com.example.a2048.domain.entity

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.example.a2048.R
import com.example.a2048.util.Helpers.Companion.string
import com.example.a2048.util.IGameSetting
import com.example.a2048.util.IModeName
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GameMode : IGameSetting, IModeName, Parcelable {
    MODE4x4 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(4, 4)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._4x4
            return context.string(id)
        }
    },
    MODE5x5 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(5, 5)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._5x5
            return context.string(id)
        }
    },
    MODE6x6 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(6, 6)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._6x6
            return context.string(id)
        }
    },
    MODE8x8 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(8, 8)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._8x8
            return context.string(id)
        }
    },
    MODE3x5 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(5, 3)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._3x5
            return context.string(id)
        }
    },
    MODE4x6 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(6, 4)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._4x6
            return context.string(id)
        }
    },
    MODE5x8 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(8, 5)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._5x8
            return context.string(id)
        }
    },
    MODE6x9 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(9, 6)
        }

        override fun modeName(context: Context): String {
            @StringRes
            val id = R.string._6x9
            return context.string(id)
        }
    };
}