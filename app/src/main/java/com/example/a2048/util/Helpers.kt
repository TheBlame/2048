package com.example.a2048.util

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.a2048.data.database.dbmodels.ScoreDbModel
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameScore
import com.example.a2048.presentation.viewmodels.Factory

class Helpers {
    companion object {
        fun Array<IntArray>.deepCopy() = Array(size) { get(it).clone() }

        @ColorInt
        fun Context.color(@ColorRes colorResId: Int) =
            ContextCompat.getColor(this, colorResId)

        inline fun <reified T : ViewModel> Fragment.lazyViewModel(
            noinline create: (savedStateHandle: SavedStateHandle) -> T
        ) = viewModels<T> {
            Factory(this, create)
        }

        fun <T> MutableList<MutableList<T>>.twoDimensionalMutableListToList() =
            map { it.toList() }.toList()

        fun <T> List<List<T>>.twoDimensionalListToMutableList() =
            map { it.toMutableList() }.toMutableList()

        fun mapGameAndDateToScoreDbModel(game: Game, date: String) =
            ScoreDbModel(date, game.score, game.gameMode)

        fun mapScoreDbModelToGameScore(scoreDbModel: ScoreDbModel) =
            GameScore(scoreDbModel.date, scoreDbModel.score)
    }
}