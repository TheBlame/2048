package com.example.a2048.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.usecases.StartGameUseCase
import com.example.a2048.domain.usecases.SwipeFieldToDirectionUseCase
import com.example.a2048.util.Direction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel @AssistedInject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val swipeFieldToDirectionUseCase: SwipeFieldToDirectionUseCase,
    @Assisted private val gameSetting: GameMode,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(startGameUseCase(gameSetting))
    val state = _state.asStateFlow()

    val swipeField: ((Direction) -> Unit) = {
        _state.value = swipeFieldToDirectionUseCase.invoke(_state.value, it)
    }

    fun startNewGame() {
        _state.value = startGameUseCase(gameSetting)
    }

    @AssistedFactory
    interface Factory {
        fun create(gameSetting: GameMode?, savedStateHandle: SavedStateHandle): GameViewModel
    }
}