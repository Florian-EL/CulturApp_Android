package com.example.culturapp_android.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Film::class, Serie::class, Roman::class, Manga::class, Webtoon::class, Wattpad::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun filmdaos(): FilmDao

    abstract fun seriedaos(): SerieDao

    abstract fun romandaos(): RomanDao

    abstract fun mangadaos(): MangaDao

    abstract fun webtoondaos(): WebtoonDao

    abstract fun wattpaddaos(): WattpadDao
}