package com.example.a2048.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.usecases.ContinueGameUseCase
import com.example.a2048.domain.usecases.SaveScoreUseCase
import com.example.a2048.domain.usecases.StartGameUseCase
import com.example.a2048.domain.usecases.SwipeFieldToDirectionUseCase
import com.example.a2048.util.Direction
import com.example.a2048.util.Helpers.Companion.buildEmptyField
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel @AssistedInject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val continueGameUseCase: ContinueGameUseCase,
    private val saveScoreUseCase: SaveScoreUseCase,
    private val swipeFieldToDirectionUseCase: SwipeFieldToDirectionUseCase,
    @Assisted private val gameMode: GameMode,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(Game(gameMode, buildEmptyField(gameMode), 0))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = continueGameUseCase(gameMode) ?: startGameUseCase(gameMode)
        }
    }

   fun swipeField(direction: Direction) {
       viewModelScope.launch {
           _state.value = swipeFieldToDirectionUseCase.invoke(_state.value, direction)
       }
    }

    fun startNewGame() {
        viewModelScope.launch {
            _state.value = startGameUseCase(gameMode)
        }
    }

    fun saveScore() {
        viewModelScope.launch(Dispatchers.IO) {
            saveScoreUseCase(_state.value)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(gameSetting: GameMode?, savedStateHandle: SavedStateHandle): GameViewModel
    }
}