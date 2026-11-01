package com.example.culturapp_android.data

object MediaCalculator {

    private fun syncTitleFields(
        legacyTitle: String?,
        principal: String?,
        secondaire: String?,
        isSerieOrFilm: Boolean = false,
        nomSerie: String? = null,
        filmName: String? = null
    ): Triple<String, String?, String?> {
        var titlePrincipal = sanitizeString(principal) ?: ""
        var titleSecondaire = sanitizeString(secondaire) ?: ""
        val rawLegacy = sanitizeString(legacyTitle) ?: ""

        if (isSerieOrFilm) {
            if (titlePrincipal.isEmpty()) {
                titlePrincipal = sanitizeString(nomSerie) ?: ""
            }
            if (titleSecondaire.isEmpty()) {
                titleSecondaire = sanitizeString(filmName) ?: ""
            }

            if (titlePrincipal.isEmpty() && rawLegacy.contains(" - ")) {
                val parts = rawLegacy.split(" - ", limit = 2)
                titlePrincipal = parts[0].trim()
                if (parts.size > 1) {
                    titleSecondaire = parts[1].trim()
                }
            }

            if (titlePrincipal.isEmpty()) {
                titlePrincipal = rawLegacy
            }
        } else {
            if (titlePrincipal.isEmpty()) {
                titlePrincipal = rawLegacy
            }
        }

        val fullTitre = if (titleSecondaire.isNotEmpty()) {
            "$titlePrincipal - $titleSecondaire"
        } else {
            titlePrincipal
        }

        return Triple(
            fullTitre,
            titlePrincipal.takeIf { it.isNotEmpty() },
            titleSecondaire.takeIf { it.isNotEmpty() }
        )
    }

    private fun sanitizeString(s: String?): String? {
        val trimmed = (s ?: "").trim()
        return if (trimmed.isEmpty() || trimmed.equals("None", ignoreCase = true) || trimmed.equals("null", ignoreCase = true) || trimmed.equals("0", ignoreCase = true)) null else trimmed
    }

    fun calculateFilm(film: Film): Film {
        val (titre, titrePrincipal, titreSecondaire) = syncTitleFields(
            legacyTitle = film.titre,
            principal = film.titre_principal,
            secondaire = film.titre_secondaire,
            isSerieOrFilm = true
        )

        var etat = if (film.note != null && film.note > 0) "FINI" else if (film.etat.isNullOrBlank()) "EN COURS" else film.etat!!
        var nbEpTot: Int? = film.nb_ep_tot
        var nbVu: Int? = film.nb_vu
        var nbEpVu: Int? = film.nb_ep_vu
        var updated = sanitizeString(film.updated)

        if (updated == "PAS SORTI") {
            nbEpTot = 0
            nbVu = 0
            nbEpVu = 0
        } else if (etat == "EN COURS") {
            if (nbEpTot == null) nbEpTot = 1
            nbEpVu = 0
            nbVu = 0
        } else if (etat == "FINI") {
            nbEpVu = 1
            nbEpTot = 1
            if (film.annee_vu != null) nbVu = 1
        }

        val nbEpRes: Int? = if (nbEpTot != null && nbEpVu != null) nbEpTot - nbEpVu else null

        if (nbEpTot == 0) {
            updated = "PAS SORTI"
            etat = ""
        } else if (nbEpTot != null && nbEpVu != null) {
            if (nbEpTot > nbEpVu) {
                etat = "EN COURS"
            } else if (nbEpTot == nbEpVu) {
                etat = "FINI"
            }
        }

        return film.copy(
            titre = titre,
            titre_principal = titrePrincipal,
            titre_secondaire = titreSecondaire,
            note = film.note?.takeIf { it > 0 },
            type = sanitizeString(film.type),
            genre = sanitizeString(film.genre),
            vo = sanitizeString(film.vo),
            notice = sanitizeString(film.notice),
            sortie = sanitizeString(film.sortie),
            etat = etat,
            updated = updated,
            nb_ep_tot = nbEpTot,
            nb_vu = nbVu,
            nb_ep_vu = nbEpVu,
            nb_ep_res = nbEpRes
        )
    }

    fun calculateSerie(serie: Serie): Serie {
        val (titre, titrePrincipal, titreSecondaire) = syncTitleFields(
            legacyTitle = serie.titre,
            principal = serie.titre_principal,
            secondaire = serie.titre_secondaire,
            isSerieOrFilm = true
        )

        var etat = if (serie.note != null && serie.note > 0) "FINI" else if (serie.etat.isNullOrBlank()) "EN COURS" else serie.etat!!
        var nbEpTot: Int? = serie.nb_ep_tot
        var nbEpVu: Int? = serie.nb_ep_vu
        var updated = sanitizeString(serie.updated)

        if (updated == "PAS SORTI") {
            nbEpTot = 0
            nbEpVu = 0
        }

        val nbEpRes: Int? = if (nbEpTot != null && nbEpVu != null) nbEpTot - nbEpVu else null

        if (nbEpTot == 0) {
            updated = "PAS SORTI"
            etat = ""
        } else if (nbEpTot != null && nbEpVu != null) {
            if (nbEpTot > nbEpVu) {
                etat = "EN COURS"
            } else if (nbEpTot == nbEpVu) {
                etat = "FINI"
            }
        }

        return serie.copy(
            titre = titre,
            titre_principal = titrePrincipal,
            titre_secondaire = titreSecondaire,
            note = serie.note?.takeIf { it > 0 },
            type = sanitizeString(serie.type),
            genre = sanitizeString(serie.genre),
            vo = sanitizeString(serie.vo),
            notice = sanitizeString(serie.notice),
            studio = sanitizeString(serie.studio),
            etat = etat,
            updated = updated,
            sortie = serie.sortie,
            nb_ep_tot = nbEpTot,
            nb_ep_vu = nbEpVu,
            nb_ep_res = nbEpRes,
            priorite = serie.priorite?.takeIf { it > 0 }
        )
    }

    fun calculateManga(manga: Manga): Manga {
        val (titre, titrePrincipal, titreSecondaire) = syncTitleFields(
            legacyTitle = manga.titre,
            principal = manga.titre_principal,
            secondaire = manga.titre_secondaire
        )

        val epAct: Int? = manga.ep_act
        val epDeb: Int? = manga.ep_deb
        var nbEpTot: Int? = manga.nb_ep_tot
        var updated = sanitizeString(manga.updated)

        var etat = if (manga.note != null && manga.note > 0) "FINI" else if (manga.etat.isNullOrBlank()) "EN COURS" else manga.etat!!

        val nbEpVu: Int? = if (epAct != null) {
            val deb = epDeb ?: 0
            val v = epAct - deb
            if (v < 0) 0 else v
        } else null

        val nbEpRes: Int? = if (nbEpTot != null && epAct != null) nbEpTot - epAct else null

        if (nbEpTot == 0) {
            updated = "PAS SORTI"
            etat = ""
        } else if (nbEpTot != null && nbEpVu != null) {
            if (nbEpTot > nbEpVu) {
                etat = "EN COURS"
            } else if (nbEpTot == nbEpVu) {
                etat = "FINI"
            }
        }

        return manga.copy(
            titre = titre,
            titre_principal = titrePrincipal,
            titre_secondaire = titreSecondaire,
            auteur = sanitizeString(manga.auteur),
            note = manga.note?.takeIf { it > 0 },
            type = sanitizeString(manga.type),
            genre = sanitizeString(manga.genre),
            vo = sanitizeString(manga.vo),
            site = sanitizeString(manga.site),
            lu_suite = sanitizeString(manga.lu_suite),
            notice = sanitizeString(manga.notice),
            etat = etat,
            updated = updated,
            nb_ep_tot = nbEpTot,
            nb_ep_vu = nbEpVu,
            nb_ep_res = nbEpRes
        )
    }

    fun calculateRoman(roman: Roman): Roman {
        val (titre, titrePrincipal, titreSecondaire) = syncTitleFields(
            legacyTitle = roman.titre,
            principal = roman.titre_principal,
            secondaire = roman.titre_secondaire
        )

        var etat = if (roman.note != null && roman.note > 0) "FINI" else if (roman.etat.isNullOrBlank()) "EN COURS" else roman.etat!!
        var nbEpTot: Int? = roman.nb_ep_tot
        var nbEpVu: Int? = roman.nb_ep_vu
        var updated = sanitizeString(roman.updated)

        if (nbEpTot == 0) {
            updated = "PAS SORTI"
            etat = ""
        } else if (nbEpTot != null && nbEpVu != null) {
            if (nbEpTot > nbEpVu) {
                etat = "EN COURS"
            } else if (nbEpTot == nbEpVu) {
                etat = "FINI"
            }
        }

        val nbEpRes: Int? = if (nbEpTot != null && nbEpVu != null) nbEpTot - nbEpVu else null

        return roman.copy(
            titre = titre,
            titre_principal = titrePrincipal,
            titre_secondaire = titreSecondaire,
            auteur = sanitizeString(roman.auteur),
            note = roman.note?.takeIf { it > 0 },
            type = sanitizeString(roman.type),
            genre = sanitizeString(roman.genre),
            notice = sanitizeString(roman.notice),
            possede = sanitizeString(roman.possede),
            etat = etat,
            updated = updated,
            nb_ep_tot = nbEpTot,
            nb_ep_vu = nbEpVu,
            nb_ep_res = nbEpRes
        )
    }

    fun calculateWebtoon(webtoon: Webtoon): Webtoon {
        val (titre, titrePrincipal, titreSecondaire) = syncTitleFields(
            legacyTitle = webtoon.titre,
            principal = webtoon.titre_principal,
            secondaire = webtoon.titre_secondaire
        )

        var etat = if (webtoon.note != null && webtoon.note > 0) "FINI" else if (webtoon.etat.isNullOrBlank()) "EN COURS" else webtoon.etat!!
        var nbEpTot: Int? = webtoon.nb_ep_tot
        var nbEpVu: Int? = webtoon.nb_ep_vu
        var updated = sanitizeString(webtoon.updated)

        if (nbEpTot == 0) {
            updated = "PAS SORTI"
            etat = ""
        } else if (nbEpTot != null && nbEpVu != null) {
            if (nbEpTot > nbEpVu) {
                etat = "EN COURS"
            } else if (nbEpTot == nbEpVu) {
                etat = "FINI"
            }
        }

        val nbEpRes: Int? = if (nbEpTot != null && nbEpVu != null) nbEpTot - nbEpVu else null

        return webtoon.copy(
            titre = titre,
            titre_principal = titrePrincipal,
            titre_secondaire = titreSecondaire,
            auteur = sanitizeString(webtoon.auteur),
            note = webtoon.note?.takeIf { it > 0 },
            type = sanitizeString(webtoon.type),
            genre = sanitizeString(webtoon.genre),
            notice = sanitizeString(webtoon.notice),
            etat = etat,
            updated = updated,
            nb_ep_tot = nbEpTot,
            nb_ep_vu = nbEpVu,
            nb_ep_res = nbEpRes
        )
    }

    fun calculateWattpad(wattpad: Wattpad): Wattpad {
        val (titre, titrePrincipal, titreSecondaire) = syncTitleFields(
            legacyTitle = wattpad.titre,
            principal = wattpad.titre_principal,
            secondaire = wattpad.titre_secondaire
        )

        var etat = if (wattpad.note != null && wattpad.note > 0) "FINI" else if (wattpad.etat.isNullOrBlank()) "EN COURS" else wattpad.etat!!
        var nbEpTot: Int? = wattpad.nb_ep_tot
        var nbEpVu: Int? = wattpad.nb_ep_vu
        var updated = sanitizeString(wattpad.updated)

        if (nbEpTot == 0) {
            updated = "PAS SORTI"
            etat = ""
        } else if (nbEpTot != null && nbEpVu != null) {
            if (nbEpTot > nbEpVu) {
                etat = "EN COURS"
            } else if (nbEpTot == nbEpVu) {
                etat = "FINI"
            }
        }

        val nbEpRes: Int? = if (nbEpTot != null && nbEpVu != null) nbEpTot - nbEpVu else null

        return wattpad.copy(
            titre = titre,
            titre_principal = titrePrincipal,
            titre_secondaire = titreSecondaire,
            auteur = sanitizeString(wattpad.auteur),
            note = wattpad.note?.takeIf { it > 0 },
            type = sanitizeString(wattpad.type),
            genre = sanitizeString(wattpad.genre),
            notice = sanitizeString(wattpad.notice),
            etat = etat,
            updated = updated,
            nb_ep_tot = nbEpTot,
            nb_ep_vu = nbEpVu,
            nb_ep_res = nbEpRes
        )
    }
}
