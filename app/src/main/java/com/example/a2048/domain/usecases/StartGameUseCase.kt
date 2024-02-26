package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository

class StartGameUseCase(private val repository: GameRepository) {

    operator fun invoke(rows: Int, columns: Int): Game {
        return repository.startGame(rows, columns)
    }
}