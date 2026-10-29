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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
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
    INIT("Initialisation"),
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun MangaScreen(
    manga: List<Manga>,
    onSaveManga: ((Manga) -> Unit)? = null,
    onDeleteManga: ((Manga) -> Unit)? = null
) {
    var selectedManga by remember { mutableStateOf<Manga?>(null) }
    var editingManga by remember { mutableStateOf<Manga?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(MangaSortOption.INIT) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedManga = remember(manga, sortOption) {
        when (sortOption) {
            MangaSortOption.INIT -> manga.sortedWith(
                compareBy<Manga> { it.etat ?: "" }.thenBy { it.updated }.thenBy { it.titre })
            MangaSortOption.TITLE_ASC -> manga.sortedBy { it.titre_principal ?: it.titre ?: "" }
            MangaSortOption.TITLE_DESC -> manga.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            MangaSortOption.NOTE_DESC -> manga.sortedByDescending { it.note ?: 0 }
            MangaSortOption.NOTE_ASC -> manga.sortedBy { it.note ?: 0 }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                items(sortedManga) { mangaItem ->
                    val coverFile = remember(
                        mangaItem.id,
                        mangaItem.titre,
                        mangaItem.titre_principal,
                        mangaItem.titre_secondaire
                    ) {
                        getCoverImageFile(context, mangaItem.id, mangaItem.titre, "manga")
                    }
                    MediaCard(
                        title = mangaItem.titre_principal,
                        secondtitre = mangaItem.titre_secondaire,
                        note = mangaItem.note,
                        nbEpVu = mangaItem.nb_ep_vu,
                        nbEpTot = mangaItem.nb_ep_tot,
                        coverFile = coverFile,
                        onClick = { selectedManga = mangaItem }
                    )
                }
            }
        }

        if (onSaveManga != null) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un manga")
            }
        }
    }

    if (showAddDialog && onSaveManga != null) {
        AddEditMangaDialog(
            manga = null,
            onDismiss = { showAddDialog = false },
            onSave = { newManga ->
                onSaveManga(newManga)
                showAddDialog = false
            }
        )
    }

    editingManga?.let { mangaToEdit ->
        if (onSaveManga != null) {
            AddEditMangaDialog(
                manga = mangaToEdit,
                onDismiss = { editingManga = null },
                onSave = { updatedManga ->
                    onSaveManga(updatedManga)
                    editingManga = null
                    selectedManga = null
                }
            )
        }
    }

    selectedManga?.let { mangaItem ->
        val coverFile = getCoverImageFile(context, mangaItem.id, mangaItem.titre, "manga")
        val details = listOf(
            "Titre" to mangaItem.titre,
            "Note" to (mangaItem.note?.takeIf { it > 0 }?.let { "$it/20" }),
            "Type" to mangaItem.type,
            "Genre" to mangaItem.genre,
            "Auteur" to mangaItem.auteur,
            "État" to mangaItem.etat,
            "Lu Suite" to mangaItem.lu_suite,
            "Nb vu" to mangaItem.nb_vu?.takeIf { it > 0 }?.toString(),
            "Site" to mangaItem.site,
            "Épisodes début" to mangaItem.ep_deb?.toString(),
            "Épisodes actuel" to mangaItem.ep_act?.toString(),
            "Épisodes restants" to mangaItem.nb_ep_res?.toString(),
            "Total épisodes" to mangaItem.nb_ep_tot?.toString(),
            "Mis à jour" to mangaItem.updated,
            "Notice" to mangaItem.notice
        )
        DetailDialog(
            title = mangaItem.titre ?: "Détails du manga",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedManga = null },
            onEdit = if (onSaveManga != null) {
                { editingManga = mangaItem }
            } else null,
            onDelete = if (onDeleteManga != null) {
                {
                    onDeleteManga(mangaItem)
                    selectedManga = null
                }
            } else null
        )
    }
}
