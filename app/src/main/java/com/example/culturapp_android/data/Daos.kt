package com.example.culturapp_android.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FilmDao {

    @Query("SELECT * FROM film")
    suspend fun getFilm(): List<Film>
}

@Dao
interface SerieDao {

    @Query("SELECT * FROM serie")
    suspend fun getSerie(): List<Serie>
}

@Dao
interface RomanDao {

    @Query("SELECT * FROM roman")
    suspend fun getRoman(): List<Roman>
}

@Dao
interface MangaDao {

    @Query("SELECT * FROM manga")
    suspend fun getManga(): List<Manga>
}

@Dao
interface WebtoonDao {

    @Query("SELECT * FROM webtoon")
    suspend fun getWebtoon(): List<Webtoon>
}

@Dao
interface WattpadDao {

    @Query("SELECT * FROM wattpad")
    suspend fun getWattpad(): List<Wattpad>
}