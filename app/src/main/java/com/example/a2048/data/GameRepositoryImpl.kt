package com.example.a2048.data

import com.example.a2048.domain.entity.Game
import com.example.a2048.domain.repository.GameRepository

class GameRepositoryImpl: GameRepository {
    override fun startGame(): Game {
        //TODO("Not yet implemented")
        return Game()
    }
}