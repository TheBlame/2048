package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameSetting
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class StartGameUseCase @Inject constructor(private val repository: GameRepository) {

    operator fun invoke(gameSetting: GameSetting, startingField: List<List<Int>>? = null): Game {
        return repository.startGame(gameSetting.rows, gameSetting.columns, startingField)
    }
}