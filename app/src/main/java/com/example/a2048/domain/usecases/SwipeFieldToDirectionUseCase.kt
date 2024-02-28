package com.example.a2048.domain.usecases

import com.example.a2048.Direction
import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository

class SwipeFieldToDirectionUseCase(private val repository: GameRepository) {

    operator fun invoke(direction: Direction) {
       repository.swipeFieldToDirection(direction)
    }
}