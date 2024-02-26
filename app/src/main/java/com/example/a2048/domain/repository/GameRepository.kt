package com.example.a2048.domain.repository

import com.example.a2048.domain.entity.Game

interface GameRepository {

    fun startGame(rows: Int, columns: Int): Game

    fun swipeLeft(): Game

    fun swipeRight(): Game

    fun swipeTop(): Game

    fun swipeBottom(): Game
}