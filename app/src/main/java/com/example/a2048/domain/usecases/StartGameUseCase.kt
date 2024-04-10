package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class StartGameUseCase @Inject constructor(private val repository: GameRepository) {

    suspend operator fun invoke(gameMode: GameMode, startingField: List<List<Int>>? = null): Game {
        return repository.startGame(gameMode, startingField)
    }
}