package com.example.a2048.domain.entity

import android.os.Parcelable
import com.example.a2048.util.IGameSetting
import com.example.a2048.util.IModeName
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GameMode : IGameSetting, IModeName, Parcelable {
    MODE4x4 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(4, 4)
        }

        override fun modeName(): String {
            return "4x4"
        }
    },
    MODE5x5 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(5, 5)
        }

        override fun modeName(): String {
            return "5x5"
        }
    },
    MODE6x6 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(6, 6)
        }

        override fun modeName(): String {
            return "6x6"
        }
    },
    MODE8x8 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(8, 8)
        }

        override fun modeName(): String {
            return "8x8"
        }
    },
    MODE3x5 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(5, 3)
        }

        override fun modeName(): String {
            return "3x5"
        }
    },
    MODE4x6 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(6, 4)
        }

        override fun modeName(): String {
            return "4x6"
        }
    },
    MODE5x8 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(8, 5)
        }

        override fun modeName(): String {
            return "5x8"
        }
    },
    MODE6x9 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(9, 6)
        }

        override fun modeName(): String {
            return "6x9"
        }
    };
}