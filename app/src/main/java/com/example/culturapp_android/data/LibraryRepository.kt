package com.example.culturapp_android.data

class LibraryRepository(
    private val filmDao: FilmDao,
    private val serieDao: SerieDao,
    private val romanDao: RomanDao,
) {

    suspend fun getFilm(): List<Film> {
        return filmDao.getFilm()
    }

    suspend fun getSerie(): List<Serie> {
        return serieDao.getSerie()
    }

    suspend fun getRoman(): List<Roman> {
        return romanDao.getRoman()
    }
}