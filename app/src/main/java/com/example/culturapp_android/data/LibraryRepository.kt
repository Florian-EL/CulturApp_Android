package com.example.culturapp_android.data

class LibraryRepository(
    private val filmDao: FilmDao,
    private val serieDao: SerieDao,
    private val romanDao: RomanDao,
    private val mangaDao: MangaDao,
    private val webtoonDao: WebtoonDao,
    private val wattpadDao: WattpadDao,
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

    suspend fun getManga(): List<Manga> {
        return mangaDao.getManga()
    }

    suspend fun getWebtoon(): List<Webtoon> {
        return webtoonDao.getWebtoon()
    }

    suspend fun getWattpad(): List<Wattpad> {
        return wattpadDao.getWattpad()
    }
}