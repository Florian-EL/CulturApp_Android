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