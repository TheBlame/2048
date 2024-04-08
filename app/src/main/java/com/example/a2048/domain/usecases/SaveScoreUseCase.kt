package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class SaveScoreUseCase @Inject constructor(private val repository: GameRepository) {

    suspend operator fun invoke(game: Game) {
        repository.saveScore(game)
    }
}