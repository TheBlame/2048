package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository
import com.example.a2048.util.Direction
import javax.inject.Inject

class SwipeFieldToDirectionUseCase @Inject constructor(private val repository: GameRepository) {

    operator fun invoke(game: Game, direction: Direction): Game {
       return repository.swipeFieldToDirection(game, direction)
    }
}