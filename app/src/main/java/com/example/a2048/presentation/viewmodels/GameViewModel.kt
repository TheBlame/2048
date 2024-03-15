package com.example.a2048.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.a2048.Utils.Direction
import com.example.a2048.domain.entity.GameSetting
import com.example.a2048.domain.usecases.StartGameUseCase
import com.example.a2048.domain.usecases.SwipeFieldToDirectionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel @AssistedInject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val swipeFieldToDirectionUseCase: SwipeFieldToDirectionUseCase,
    @Assisted private val gameSetting: GameSetting,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(startGameUseCase(gameSetting))
    val state = _state.asStateFlow()

    val swipeField: ((Direction) -> Unit) = {
        _state.value = swipeFieldToDirectionUseCase.invoke(_state.value, it)
    }

    @AssistedFactory
    interface Factory {
        fun create(gameSetting: GameSetting, savedStateHandle: SavedStateHandle): GameViewModel
    }
}