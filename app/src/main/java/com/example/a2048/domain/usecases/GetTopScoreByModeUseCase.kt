package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.GameMode
import com.example.a2048.domain.repository.GameRepository
import javax.inject.Inject

class GetTopScoreByModeUseCase @Inject constructor(val repository: GameRepository) {

    suspend operator fun invoke(mode: GameMode): Int {
        return repository.getTopScoreByMode(mode)
    }
}