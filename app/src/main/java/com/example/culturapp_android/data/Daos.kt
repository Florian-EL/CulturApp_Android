package com.example.culturapp_android.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FilmDao {

    @Query("SELECT * FROM film")
    suspend fun getFilm(): List<Film>
}