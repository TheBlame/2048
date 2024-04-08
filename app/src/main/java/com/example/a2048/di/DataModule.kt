package com.example.a2048.di

import android.app.Application
import com.example.a2048.data.GameRepositoryImpl
import com.example.a2048.data.database.AppDatabase
import com.example.a2048.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindGameRepositoryImp(impl: GameRepositoryImpl): GameRepository

    companion object {

        @ApplicationScope
        @Provides
        fun provideDb(application: Application): AppDatabase {
            return AppDatabase.getInstance(application)
        }
    }
}