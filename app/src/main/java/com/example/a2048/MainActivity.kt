package com.example.a2048

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.a2048.data.GameRepositoryImpl
import com.example.a2048.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gr = GameRepositoryImpl()
        val game = gr.startGame(4, 4)

        binding.gameField.gameField = game.field
        binding.gameField.setSwipeListener {
            gr.swipeFieldToDirection(it)
            binding.gameField.gameField = game.field
        }
    }
}