package com.example.a2048.domain.usecases

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository

class SwipeRightUseCase (private val repository: GameRepository) {

    operator fun invoke(): Game {
        return repository.swipeRight()
    }
}