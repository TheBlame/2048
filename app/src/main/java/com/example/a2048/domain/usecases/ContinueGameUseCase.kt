package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class ContinueGameUseCase @Inject constructor(val repository: GameRepository) {

    suspend operator fun invoke(gameMode: GameMode): Game? {
        return repository.continueGame(gameMode)
    }
}