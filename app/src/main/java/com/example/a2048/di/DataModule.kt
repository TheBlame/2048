package com.example.a2048.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.a2048.data.GameRepositoryImpl
import com.example.a2048.data.database.AppDatabase
import com.example.a2048.data.datastore.SavedGames
import com.example.a2048.data.datastore.SavedGamesSerializer
import com.example.a2048.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

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

        @ApplicationScope
        @Provides
        fun provideProtoDataStore(application: Application): DataStore<SavedGames> {
            return DataStoreFactory.create(
                serializer = SavedGamesSerializer,
                produceFile = { application.applicationContext.dataStoreFile(DATA_STORE_FILE_NAME) },
                corruptionHandler = null,
                scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            )
        }
    }
}