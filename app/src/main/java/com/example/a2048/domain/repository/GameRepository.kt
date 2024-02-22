package com.example.a2048.domain.repository

import com.example.a2048.domain.entity.Game

interface GameRepository {

    fun startGame(): Game
}