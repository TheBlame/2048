package com.example.a2048.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.usecases.GetTopScoreByModeUseCase
import com.example.a2048.domain.usecases.SaveScoreUseCase
import com.example.a2048.domain.usecases.StartGameUseCase
import com.example.a2048.domain.usecases.SwipeFieldToDirectionUseCase
import com.example.a2048.util.Direction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel @AssistedInject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val getTopScoreByModeUseCase: GetTopScoreByModeUseCase,
    private val saveScoreUseCase: SaveScoreUseCase,
    private val swipeFieldToDirectionUseCase: SwipeFieldToDirectionUseCase,
    @Assisted private val gameMode: GameMode,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var topScore = 0

    private val _state = MutableStateFlow(GameVmState(startGameUseCase(gameMode), topScore))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            topScore = getTopScoreByModeUseCase(gameMode)
            _state.value = _state.value.copy(topScore = topScore)
        }
    }

    val swipeField: ((Direction) -> Unit) = {
        _state.value = _state.value.copy(game = swipeFieldToDirectionUseCase.invoke(_state.value.game, it))
    }

    fun startNewGame() {
        _state.value = _state.value.copy(game = startGameUseCase(gameMode))
    }

    suspend fun saveScore() {
        saveScoreUseCase.invoke(_state.value.game)
    }

    @AssistedFactory
    interface Factory {
        fun create(gameSetting: GameMode?, savedStateHandle: SavedStateHandle): GameViewModel
    }

    data class GameVmState(
        val game: Game,
        val topScore: Int
    )
}