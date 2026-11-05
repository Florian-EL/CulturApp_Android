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
import com.example.culturapp_android.data.Roman

enum class RomanSortOption(val label: String) {
    INIT("Initialisation"),
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun RomanScreen(
    roman: List<Roman>,
    imagesVersion: Int = 0,
    onSaveRoman: ((Roman) -> Unit)? = null,
    onDeleteRoman: ((Roman) -> Unit)? = null
) {
    var selectedRoman by remember { mutableStateOf<Roman?>(null) }
    var editingRoman by remember { mutableStateOf<Roman?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(RomanSortOption.INIT) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedRoman = remember(roman, sortOption) {
        when (sortOption) {
            RomanSortOption.INIT -> roman.sortedWith(
                compareBy<Roman> { it.etat ?: "" }.thenBy { it.updated ?: "" }.thenBy { it.titre })
            RomanSortOption.TITLE_ASC -> roman.sortedBy { it.titre_principal ?: it.titre ?: "" }
            RomanSortOption.TITLE_DESC -> roman.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            RomanSortOption.NOTE_DESC -> roman.sortedByDescending { it.note ?: 0f }
            RomanSortOption.NOTE_ASC -> roman.sortedBy { it.note ?: 0f }
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
                    text = "Roman (${roman.size})",
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
                        RomanSortOption.entries.forEach { option ->
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
                items(sortedRoman) { romanItem ->
                    val coverFile = remember(
                        romanItem.id,
                        romanItem.titre,
                        romanItem.titre_principal,
                        romanItem.titre_secondaire,
                        imagesVersion
                    ) {
                        getCoverImageFile(context, romanItem.id, romanItem.titre, "roman")
                    }
                    MediaCard(
                        title = romanItem.titre_principal,
                        secondtitre = romanItem.titre_secondaire,
                        note = romanItem.note,
                        nbEpVu = romanItem.nb_ep_vu,
                        nbEpTot = romanItem.nb_ep_tot,
                        coverFile = coverFile,
                        onClick = { selectedRoman = romanItem }
                    )
                }
            }
        }

        if (onSaveRoman != null) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un roman")
            }
        }
    }

    if (showAddDialog && onSaveRoman != null) {
        AddEditRomanDialog(
            roman = null,
            onDismiss = { showAddDialog = false },
            onSave = { newRoman ->
                onSaveRoman(newRoman)
                showAddDialog = false
            }
        )
    }

    editingRoman?.let { romanToEdit ->
        if (onSaveRoman != null) {
            AddEditRomanDialog(
                roman = romanToEdit,
                onDismiss = { editingRoman = null },
                onSave = { updatedRoman ->
                    onSaveRoman(updatedRoman)
                    editingRoman = null
                    selectedRoman = null
                }
            )
        }
    }

    selectedRoman?.let { romanItem ->
        val coverFile = getCoverImageFile(context, romanItem.id, romanItem.titre, "roman")
        val details = listOf(
            "Titre" to romanItem.titre,
            "Note" to (romanItem.note?.takeIf { it > 0 }?.let { "$it/20" }),
            "Type" to romanItem.type,
            "Genre" to romanItem.genre,
            "Auteur" to romanItem.auteur,
            "État" to romanItem.etat,
            "Possédé" to romanItem.possede,
            "Nb vu" to romanItem.nb_vu?.takeIf { it > 0 }?.toString(),
            "Épisodes vus" to romanItem.nb_ep_vu?.toString(),
            "Épisodes restants" to romanItem.nb_ep_res?.toString(),
            "Total épisodes" to romanItem.nb_ep_tot?.toString(),
            "Mis à jour" to romanItem.updated,
            "Notice" to romanItem.notice
        )
        DetailDialog(
            title = romanItem.titre ?: "Détails du roman",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedRoman = null },
            onEdit = if (onSaveRoman != null) {
                { editingRoman = romanItem }
            } else null,
            onDelete = if (onDeleteRoman != null) {
                {
                    onDeleteRoman(romanItem)
                    selectedRoman = null
                }
            } else null
        )
    }
}
