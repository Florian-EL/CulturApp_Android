package com.example.culturapp_android.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Film::class, Serie::class, Roman::class, Manga::class, Webtoon::class, Wattpad::class, Citation::class],
    version = 9,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ]
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun filmdaos(): FilmDao

    abstract fun seriedaos(): SerieDao

    abstract fun romandaos(): RomanDao

    abstract fun mangadaos(): MangaDao

    abstract fun webtoondaos(): WebtoonDao

    abstract fun wattpaddaos(): WattpadDao

    abstract fun citationdaos(): CitationDao
}