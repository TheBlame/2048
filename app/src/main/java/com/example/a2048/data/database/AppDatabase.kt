package com.example.a2048.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.a2048.data.database.dbmodels.ScoreDbModel

@Database(
    entities = [ScoreDbModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private const val DB_NAME = "main.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DB_NAME
            ).build()
    }

    abstract fun dbDao(): DbDao
}