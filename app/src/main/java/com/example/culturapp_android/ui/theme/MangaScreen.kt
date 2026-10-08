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
import com.example.culturapp_android.data.Manga

enum class MangaSortOption(val label: String) {
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun MangaScreen(
    manga: List<Manga>,
) {
    var selectedManga by remember { mutableStateOf<Manga?>(null) }
    var sortOption by remember { mutableStateOf(MangaSortOption.TITLE_ASC) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedManga = remember(manga, sortOption) {
        when (sortOption) {
            MangaSortOption.TITLE_ASC -> manga.sortedBy { it.titre_principal ?: it.titre ?: "" }
            MangaSortOption.TITLE_DESC -> manga.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            MangaSortOption.NOTE_DESC -> manga.sortedByDescending { it.note ?: 0 }
            MangaSortOption.NOTE_ASC -> manga.sortedBy { it.note ?: 0 }
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
                text = "Manga (${manga.size})",
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
                    MangaSortOption.entries.forEach { option ->
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
            items(sortedManga) { manga ->
                val coverFile = remember(manga.id, manga.titre) {
                    getCoverImageFile(context, manga.id, manga.titre, "manga")
                }
                MediaCard(
                    title = manga.titre_principal,
                    secondtitre = manga.titre_secondaire,
                    note = manga.note,
                    nbEpVu = manga.nb_ep_vu,
                    nbEpTot = manga.nb_ep_tot,
                    coverFile = coverFile,
                    onClick = { selectedManga = manga }
                )
            }
        }
    }

    selectedManga?.let { manga ->
        val coverFile = getCoverImageFile(context, manga.id, manga.titre, "manga")
        val details = listOf(
            "Titre" to manga.titre,
            "Note" to (manga.note?.let { "$it/20" }),
            "Type" to manga.type,
            "Genre" to manga.genre,
            "Auteur" to manga.auteur,
            "État" to manga.etat,
            "Lu Suite" to manga.lu_suite,
            "Nb vu" to manga.nb_vu?.toString(),
            "Site" to manga.site,
            "Épisodes début" to manga.ep_deb?.toString(),
            "Épisodes actuel" to manga.ep_act?.toString(),
            "Épisodes restants" to manga.nb_ep_res?.toString(),
            "Total épisodes" to manga.nb_ep_tot?.toString(),
            "Mis à jour" to manga.updated,
            "Notice" to manga.notice
        )
        DetailDialog(
            title = manga.titre ?: "Détails du manga",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedManga = null }
        )
    }
}
