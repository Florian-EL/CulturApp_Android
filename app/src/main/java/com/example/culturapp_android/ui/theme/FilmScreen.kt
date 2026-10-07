package com.example.culturapp_android.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.culturapp_android.data.Film

enum class FilmSortOption(val label: String) {
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun FilmScreen(
    film: List<Film>,
) {
    var selectedFilm by remember { mutableStateOf<Film?>(null) }
    var sortOption by remember { mutableStateOf(FilmSortOption.TITLE_ASC) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedUsers = remember(film, sortOption) {
        when (sortOption) {
            FilmSortOption.TITLE_ASC -> film.sortedBy { it.titre_principal ?: it.titre ?: "" }
            FilmSortOption.TITLE_DESC -> film.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            FilmSortOption.NOTE_DESC -> film.sortedByDescending { it.note ?: 0 }
            FilmSortOption.NOTE_ASC -> film.sortedBy { it.note ?: 0 }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Films (${film.size})",
                style = MaterialTheme.typography.headlineSmall
            )

            Box {
                OutlinedButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Trier")
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    FilmSortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                sortOption = option
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedUsers) { film ->
                val coverFile = remember(film.id, film.titre) {
                    getCoverImageFile(context, film.id, film.titre, "film")
                }
                MediaCard(
                    title = film.titre_principal,
                    secondtitre = film.titre_secondaire,
                    note = film.note,
                    nbEpVu = film.nb_ep_vu,
                    nbEpTot = film.nb_ep_tot,
                    coverFile = coverFile,
                    onClick = { selectedFilm = film }
                )
            }
        }
    }

    selectedFilm?.let { film ->
        val coverFile = getCoverImageFile(context, film.id, film.titre, "film")
        val details = listOf(
            "Titre" to film.titre,
            "Note" to (film.note?.let { "$it/20" }),
            "Type" to film.type,
            "Genre" to film.genre,
            "VO" to film.vo,
            "Cinéma" to film.cinema?.toString(),
            "État" to film.etat,
            "Année vu" to film.annee_vu?.toString(),
            "Nb vu" to film.nb_vu?.toString(),
            "Sortie" to film.sortie,
            "Épisodes vus" to film.nb_ep_vu?.toString(),
            "Épisodes restants" to film.nb_ep_res?.toString(),
            "Total épisodes" to film.nb_ep_tot?.toString(),
            "Mis à jour" to film.updated,
            "Notice" to film.notice
        )
        DetailDialog(
            title = film.titre ?: "Détails du film",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedFilm = null }
        )
    }
}
