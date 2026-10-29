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
import com.example.culturapp_android.data.Webtoon

enum class WebtoonSortOption(val label: String) {
    INIT("Initialisation"),
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun WebtoonScreen(
    webtoon: List<Webtoon>,
    onSaveWebtoon: ((Webtoon) -> Unit)? = null,
    onDeleteWebtoon: ((Webtoon) -> Unit)? = null
) {
    var selectedWebtoon by remember { mutableStateOf<Webtoon?>(null) }
    var editingWebtoon by remember { mutableStateOf<Webtoon?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(WebtoonSortOption.INIT) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedWebtoon = remember(webtoon, sortOption) {
        when (sortOption) {
            WebtoonSortOption.INIT -> webtoon.sortedWith(
                compareBy<Webtoon> { it.etat ?: "" }.thenBy { it.updated }.thenBy { it.titre })
            WebtoonSortOption.TITLE_ASC -> webtoon.sortedBy { it.titre_principal ?: it.titre ?: "" }
            WebtoonSortOption.TITLE_DESC -> webtoon.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            WebtoonSortOption.NOTE_DESC -> webtoon.sortedByDescending { it.note ?: 0 }
            WebtoonSortOption.NOTE_ASC -> webtoon.sortedBy { it.note ?: 0 }
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
                    text = "Webtoon (${webtoon.size})",
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
                        WebtoonSortOption.entries.forEach { option ->
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
                items(sortedWebtoon) { webtoonItem ->
                    val coverFile = remember(
                        webtoonItem.id,
                        webtoonItem.titre,
                        webtoonItem.titre_principal,
                        webtoonItem.titre_secondaire
                    ) {
                        getCoverImageFile(context, webtoonItem.id, webtoonItem.titre, "webtoon")
                    }
                    MediaCard(
                        title = webtoonItem.titre_principal,
                        secondtitre = webtoonItem.titre_secondaire,
                        note = webtoonItem.note,
                        nbEpVu = webtoonItem.nb_ep_vu,
                        nbEpTot = webtoonItem.nb_ep_tot,
                        coverFile = coverFile,
                        onClick = { selectedWebtoon = webtoonItem }
                    )
                }
            }
        }

        if (onSaveWebtoon != null) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un webtoon")
            }
        }
    }

    if (showAddDialog && onSaveWebtoon != null) {
        AddEditWebtoonDialog(
            webtoon = null,
            onDismiss = { showAddDialog = false },
            onSave = { newWebtoon ->
                onSaveWebtoon(newWebtoon)
                showAddDialog = false
            }
        )
    }

    editingWebtoon?.let { webtoonToEdit ->
        if (onSaveWebtoon != null) {
            AddEditWebtoonDialog(
                webtoon = webtoonToEdit,
                onDismiss = { editingWebtoon = null },
                onSave = { updatedWebtoon ->
                    onSaveWebtoon(updatedWebtoon)
                    editingWebtoon = null
                    selectedWebtoon = null
                }
            )
        }
    }

    selectedWebtoon?.let { webtoonItem ->
        val coverFile = getCoverImageFile(context, webtoonItem.id, webtoonItem.titre, "webtoon")
        val details = listOf(
            "Titre" to webtoonItem.titre,
            "Note" to (webtoonItem.note?.takeIf { it > 0 }?.let { "$it/20" }),
            "Type" to webtoonItem.type,
            "Genre" to webtoonItem.genre,
            "Auteur" to webtoonItem.auteur,
            "État" to webtoonItem.etat,
            "Nb vu" to webtoonItem.nb_vu?.takeIf { it > 0 }?.toString(),
            "Épisodes restants" to webtoonItem.nb_ep_res?.toString(),
            "Total épisodes" to webtoonItem.nb_ep_tot?.toString(),
            "Mis à jour" to webtoonItem.updated,
            "Notice" to webtoonItem.notice
        )
        DetailDialog(
            title = webtoonItem.titre ?: "Détails du webtoon",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedWebtoon = null },
            onEdit = if (onSaveWebtoon != null) {
                { editingWebtoon = webtoonItem }
            } else null,
            onDelete = if (onDeleteWebtoon != null) {
                {
                    onDeleteWebtoon(webtoonItem)
                    selectedWebtoon = null
                }
            } else null
        )
    }
}
