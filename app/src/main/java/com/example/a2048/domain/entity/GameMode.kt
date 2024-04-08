package com.example.a2048.domain.entity

import android.os.Parcelable
import com.example.a2048.util.IGameSetting
import kotlinx.parcelize.Parcelize

@Parcelize
enum class GameMode : IGameSetting, Parcelable {
    MODE4x4 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(4, 4)
        }
    },
    MODE5x5 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(5, 5)
        }
    },
    MODE6x6 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(6, 6)
        }
    },
    MODE8x8 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(8, 8)
        }
    },
    MODE3x5 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(5, 3)
        }
    },
    MODE4x6 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(6, 4)
        }
    },
    MODE5x8 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(8, 5)
        }
    },
    MODE6x9 {
        override fun getGameSetting(): GameSetting {
            return GameSetting(9, 6)
        }
    };
}