package com.example.culturapp_android.data

class LibraryRepository(
    private val filmDao: FilmDao,
    private val serieDao: SerieDao,
    private val romanDao: RomanDao,
    private val mangaDao: MangaDao,
    private val webtoonDao: WebtoonDao,
    private val wattpadDao: WattpadDao,
) {

    suspend fun getFilm(): List<Film> = filmDao.getFilm()
    suspend fun getSerie(): List<Serie> = serieDao.getSerie()
    suspend fun getRoman(): List<Roman> = romanDao.getRoman()
    suspend fun getManga(): List<Manga> = mangaDao.getManga()
    suspend fun getWebtoon(): List<Webtoon> = webtoonDao.getWebtoon()
    suspend fun getWattpad(): List<Wattpad> = wattpadDao.getWattpad()

    suspend fun saveFilm(film: Film): Long {
        val calculated = MediaCalculator.calculateFilm(film)
        return filmDao.insertFilm(calculated)
    }

    suspend fun saveSerie(serie: Serie): Long {
        val calculated = MediaCalculator.calculateSerie(serie)
        return serieDao.insertSerie(calculated)
    }

    suspend fun saveRoman(roman: Roman): Long {
        val calculated = MediaCalculator.calculateRoman(roman)
        return romanDao.insertRoman(calculated)
    }

    suspend fun saveManga(manga: Manga): Long {
        val calculated = MediaCalculator.calculateManga(manga)
        return mangaDao.insertManga(calculated)
    }

    suspend fun saveWebtoon(webtoon: Webtoon): Long {
        val calculated = MediaCalculator.calculateWebtoon(webtoon)
        return webtoonDao.insertWebtoon(calculated)
    }

    suspend fun saveWattpad(wattpad: Wattpad): Long {
        val calculated = MediaCalculator.calculateWattpad(wattpad)
        return wattpadDao.insertWattpad(calculated)
    }

    suspend fun deleteFilm(film: Film) = filmDao.deleteFilm(film)
    suspend fun deleteSerie(serie: Serie) = serieDao.deleteSerie(serie)
    suspend fun deleteRoman(roman: Roman) = romanDao.deleteRoman(roman)
    suspend fun deleteManga(manga: Manga) = mangaDao.deleteManga(manga)
    suspend fun deleteWebtoon(webtoon: Webtoon) = webtoonDao.deleteWebtoon(webtoon)
    suspend fun deleteWattpad(wattpad: Wattpad) = wattpadDao.deleteWattpad(wattpad)
}