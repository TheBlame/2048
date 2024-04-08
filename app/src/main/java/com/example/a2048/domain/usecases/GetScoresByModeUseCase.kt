package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.entity.GameScore
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class GetScoresByModeUseCase @Inject constructor(val repository: GameRepository) {

    suspend operator fun invoke(mode: GameMode): List<GameScore> {
        return repository.getScoresByMode(mode)
    }
}