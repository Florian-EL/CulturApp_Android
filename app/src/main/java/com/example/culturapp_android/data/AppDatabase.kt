package com.example.culturapp_android.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Film::class, Serie::class, Roman::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun filmdaos(): FilmDao

    abstract fun seriedaos(): SerieDao

    abstract fun romandaos(): RomanDao
}