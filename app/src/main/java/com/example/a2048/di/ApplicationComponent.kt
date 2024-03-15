package com.example.a2048.di

import android.app.Application
import com.example.a2048.presentation.viewmodels.GameViewModel
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class])
interface ApplicationComponent {

    fun gameViewModel(): GameViewModel.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}