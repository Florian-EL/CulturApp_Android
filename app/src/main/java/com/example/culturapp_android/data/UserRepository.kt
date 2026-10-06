package com.example.culturapp_android.data

class UserRepository(
    private val dao: FilmDao
) {

    suspend fun getFilm(): List<Film> {
        return dao.getFilm()
    }
}