package com.example.a2048.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.entity.GameScore
import com.example.a2048.domain.usecases.GetScoresByModeUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainMenuViewModel @AssistedInject constructor(
    private val getScoresByModeUseCase: GetScoresByModeUseCase,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _scoreList = MutableStateFlow(listOf<GameScore>())
    val scoreList = _scoreList.asStateFlow()

    suspend fun getScoreList(mode: GameMode) {
        _scoreList.value = getScoresByModeUseCase(mode)
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): MainMenuViewModel
    }
}