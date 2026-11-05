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
import com.example.culturapp_android.data.Serie

enum class SerieSortOption(val label: String) {
    INIT("Initialisation"),
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun SerieScreen(
    serie: List<Serie>,
    imagesVersion: Int = 0,
    onSaveSerie: ((Serie) -> Unit)? = null,
    onDeleteSerie: ((Serie) -> Unit)? = null
) {
    var selectedSerie by remember { mutableStateOf<Serie?>(null) }
    var editingSerie by remember { mutableStateOf<Serie?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SerieSortOption.INIT) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedSerie = remember(serie, sortOption) {
        when (sortOption) {
            SerieSortOption.INIT -> serie.sortedWith(
                compareBy<Serie> { it.updated ?: "" }.thenBy { it.etat }.thenBy { it.priorite }
                    .thenBy { it.titre })
            SerieSortOption.TITLE_ASC -> serie.sortedBy { it.titre_principal ?: it.titre ?: "" }
            SerieSortOption.TITLE_DESC -> serie.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            SerieSortOption.NOTE_DESC -> serie.sortedByDescending { it.note ?: 0f }
            SerieSortOption.NOTE_ASC -> serie.sortedBy { it.note ?: 0f }
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
                    text = "Séries (${serie.size})",
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
                        SerieSortOption.entries.forEach { option ->
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
                items(sortedSerie) { serieItem ->
                    val coverFile = remember(
                        serieItem.id,
                        serieItem.titre,
                        serieItem.titre_principal,
                        serieItem.titre_secondaire,
                        imagesVersion
                    ) {
                        getCoverImageFile(context, serieItem.id, serieItem.titre, "serie")
                    }
                    MediaCard(
                        title = serieItem.titre_principal,
                        secondtitre = serieItem.titre_secondaire,
                        note = serieItem.note,
                        nbEpVu = serieItem.nb_ep_vu,
                        nbEpTot = serieItem.nb_ep_tot,
                        coverFile = coverFile,
                        onClick = { selectedSerie = serieItem }
                    )
                }
            }
        }

        if (onSaveSerie != null) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter une série")
            }
        }
    }

    if (showAddDialog && onSaveSerie != null) {
        AddEditSerieDialog(
            serie = null,
            onDismiss = { showAddDialog = false },
            onSave = { newSerie ->
                onSaveSerie(newSerie)
                showAddDialog = false
            }
        )
    }

    editingSerie?.let { serieToEdit ->
        if (onSaveSerie != null) {
            AddEditSerieDialog(
                serie = serieToEdit,
                onDismiss = { editingSerie = null },
                onSave = { updatedSerie ->
                    onSaveSerie(updatedSerie)
                    editingSerie = null
                    selectedSerie = null
                }
            )
        }
    }

    selectedSerie?.let { serieItem ->
        val coverFile = getCoverImageFile(context, serieItem.id, serieItem.titre, "serie")
        val details = listOf(
            "Titre" to serieItem.titre,
            "Note" to (serieItem.note?.takeIf { it > 0 }?.let { "$it/20" }),
            "Type" to serieItem.type,
            "Genre" to serieItem.genre,
            "VO" to serieItem.vo,
            "Studio" to serieItem.studio,
            "État" to serieItem.etat,
            "Sortie" to serieItem.sortie,
            "Nb vu" to serieItem.nb_vu?.takeIf { it > 0 }?.toString(),
            "Épisodes vus" to serieItem.nb_ep_vu?.toString(),
            "Épisodes restants" to serieItem.nb_ep_res?.toString(),
            "Total épisodes" to serieItem.nb_ep_tot?.toString(),
            "Priorité" to serieItem.priorite?.takeIf { it > 0 }?.toString(),
            "Mis à jour" to serieItem.updated,
            "Notice" to serieItem.notice
        )
        DetailDialog(
            title = serieItem.titre ?: "Détails de la série",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedSerie = null },
            onEdit = if (onSaveSerie != null) {
                { editingSerie = serieItem }
            } else null,
            onDelete = if (onDeleteSerie != null) {
                {
                    onDeleteSerie(serieItem)
                    selectedSerie = null
                }
            } else null
        )
    }
}
