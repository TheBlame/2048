package com.example.a2048

import android.app.Application
import com.example.a2048.di.DaggerApplicationComponent

class App2048 : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}