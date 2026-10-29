package com.example.culturapp_android.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FilmDao {

    @Query("SELECT * FROM film")
    suspend fun getFilm(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilm(film: Film): Long

    @Update
    suspend fun updateFilm(film: Film)

    @Delete
    suspend fun deleteFilm(film: Film)
}

@Dao
interface SerieDao {

    @Query("SELECT * FROM serie")
    suspend fun getSerie(): List<Serie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSerie(serie: Serie): Long

    @Update
    suspend fun updateSerie(serie: Serie)

    @Delete
    suspend fun deleteSerie(serie: Serie)
}

@Dao
interface RomanDao {

    @Query("SELECT * FROM roman")
    suspend fun getRoman(): List<Roman>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoman(roman: Roman): Long

    @Update
    suspend fun updateRoman(roman: Roman)

    @Delete
    suspend fun deleteRoman(roman: Roman)
}

@Dao
interface MangaDao {

    @Query("SELECT * FROM manga")
    suspend fun getManga(): List<Manga>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: Manga): Long

    @Update
    suspend fun updateManga(manga: Manga)

    @Delete
    suspend fun deleteManga(manga: Manga)
}

@Dao
interface WebtoonDao {

    @Query("SELECT * FROM webtoon")
    suspend fun getWebtoon(): List<Webtoon>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWebtoon(webtoon: Webtoon): Long

    @Update
    suspend fun updateWebtoon(webtoon: Webtoon)

    @Delete
    suspend fun deleteWebtoon(webtoon: Webtoon)
}

@Dao
interface WattpadDao {

    @Query("SELECT * FROM wattpad")
    suspend fun getWattpad(): List<Wattpad>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWattpad(wattpad: Wattpad): Long

    @Update
    suspend fun updateWattpad(wattpad: Wattpad)

    @Delete
    suspend fun deleteWattpad(wattpad: Wattpad)
}