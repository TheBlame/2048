package com.example.a2048.di

import com.example.a2048.data.GameRepositoryImpl
import com.example.a2048.domain.repository.GameRepository
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindGameRepositoryImp(impl: GameRepositoryImpl): GameRepository
}