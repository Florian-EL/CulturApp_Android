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
import com.example.culturapp_android.data.Wattpad

enum class WattpadSortOption(val label: String) {
    INIT("Initialisation"),
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun WattpadScreen(
    wattpad: List<Wattpad>,
    imagesVersion: Int = 0,
    onSaveWattpad: ((Wattpad) -> Unit)? = null,
    onDeleteWattpad: ((Wattpad) -> Unit)? = null
) {
    var selectedWattpad by remember { mutableStateOf<Wattpad?>(null) }
    var editingWattpad by remember { mutableStateOf<Wattpad?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(WattpadSortOption.INIT) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedWattpad = remember(wattpad, sortOption) {
        when (sortOption) {
            WattpadSortOption.INIT -> wattpad.sortedWith(
                compareBy<Wattpad> { it.etat ?: "" }.thenBy { it.updated }.thenBy { it.titre })
            WattpadSortOption.TITLE_ASC -> wattpad.sortedBy { it.titre_principal ?: it.titre ?: "" }
            WattpadSortOption.TITLE_DESC -> wattpad.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            WattpadSortOption.NOTE_DESC -> wattpad.sortedByDescending { it.note ?: 0 }
            WattpadSortOption.NOTE_ASC -> wattpad.sortedBy { it.note ?: 0 }
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
                    text = "Wattpad (${wattpad.size})",
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
                        WattpadSortOption.entries.forEach { option ->
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
                items(sortedWattpad) { wattpadItem ->
                    val coverFile = remember(
                        wattpadItem.id,
                        wattpadItem.titre,
                        wattpadItem.titre_principal,
                        wattpadItem.titre_secondaire,
                        imagesVersion
                    ) {
                        getCoverImageFile(context, wattpadItem.id, wattpadItem.titre, "wattpad")
                    }
                    MediaCard(
                        title = wattpadItem.titre_principal,
                        secondtitre = wattpadItem.titre_secondaire,
                        note = wattpadItem.note,
                        nbEpVu = wattpadItem.nb_ep_vu,
                        nbEpTot = wattpadItem.nb_ep_tot,
                        coverFile = coverFile,
                        onClick = { selectedWattpad = wattpadItem }
                    )
                }
            }
        }

        if (onSaveWattpad != null) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter sur Wattpad")
            }
        }
    }

    if (showAddDialog && onSaveWattpad != null) {
        AddEditWattpadDialog(
            wattpad = null,
            onDismiss = { showAddDialog = false },
            onSave = { newWattpad ->
                onSaveWattpad(newWattpad)
                showAddDialog = false
            }
        )
    }

    editingWattpad?.let { wattpadToEdit ->
        if (onSaveWattpad != null) {
            AddEditWattpadDialog(
                wattpad = wattpadToEdit,
                onDismiss = { editingWattpad = null },
                onSave = { updatedWattpad ->
                    onSaveWattpad(updatedWattpad)
                    editingWattpad = null
                    selectedWattpad = null
                }
            )
        }
    }

    selectedWattpad?.let { wattpadItem ->
        val coverFile = getCoverImageFile(context, wattpadItem.id, wattpadItem.titre, "wattpad")
        val details = listOf(
            "Titre" to wattpadItem.titre,
            "Note" to (wattpadItem.note?.takeIf { it > 0 }?.let { "$it/20" }),
            "Type" to wattpadItem.type,
            "Genre" to wattpadItem.genre,
            "Auteur" to wattpadItem.auteur,
            "État" to wattpadItem.etat,
            "Nb vu" to wattpadItem.nb_vu?.takeIf { it > 0 }?.toString(),
            "Épisodes restants" to wattpadItem.nb_ep_res?.toString(),
            "Total épisodes" to wattpadItem.nb_ep_tot?.toString(),
            "Mis à jour" to wattpadItem.updated,
            "Notice" to wattpadItem.notice
        )
        DetailDialog(
            title = wattpadItem.titre ?: "Détails du wattpad",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedWattpad = null },
            onEdit = if (onSaveWattpad != null) {
                { editingWattpad = wattpadItem }
            } else null,
            onDelete = if (onDeleteWattpad != null) {
                {
                    onDeleteWattpad(wattpadItem)
                    selectedWattpad = null
                }
            } else null
        )
    }
}
