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
import com.example.culturapp_android.data.Roman

enum class RomanSortOption(val label: String) {
    TITLE_ASC("Titre (A-Z)"),
    TITLE_DESC("Titre (Z-A)"),
    NOTE_DESC("Note (Décroissante)"),
    NOTE_ASC("Note (Croissante)"),
}

@Composable
fun RomanScreen(
    roman: List<Roman>,
) {
    var selectedRoman by remember { mutableStateOf<Roman?>(null) }
    var sortOption by remember { mutableStateOf(RomanSortOption.TITLE_ASC) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sortedRoman = remember(roman, sortOption) {
        when (sortOption) {
            RomanSortOption.TITLE_ASC -> roman.sortedBy { it.titre_principal ?: it.titre ?: "" }
            RomanSortOption.TITLE_DESC -> roman.sortedByDescending {
                it.titre_principal ?: it.titre ?: ""
            }

            RomanSortOption.NOTE_DESC -> roman.sortedByDescending { it.note ?: 0 }
            RomanSortOption.NOTE_ASC -> roman.sortedBy { it.note ?: 0 }
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
            items(sortedRoman) { roman ->
                val coverFile = remember(roman.id, roman.titre) {
                    getCoverImageFile(context, roman.id, roman.titre, "roman")
                }
                MediaCard(
                    title = roman.titre_principal,
                    secondtitre = roman.titre_secondaire,
                    note = roman.note,
                    nbEpVu = roman.nb_ep_vu,
                    nbEpTot = roman.nb_ep_tot,
                    coverFile = coverFile,
                    onClick = { selectedRoman = roman }
                )
            }
        }
    }

    selectedRoman?.let { roman ->
        val coverFile = getCoverImageFile(context, roman.id, roman.titre, "roman")
        val details = listOf(
            "Titre" to roman.titre,
            "Note" to (roman.note?.let { "$it/20" }),
            "Type" to roman.type,
            "Genre" to roman.genre,
            "Auteur" to roman.auteur,
            "État" to roman.etat,
            "Possédé" to roman.possede,
            "Nb vu" to roman.nb_vu?.toString(),
            "Épisodes vus" to roman.nb_ep_vu?.toString(),
            "Épisodes restants" to roman.nb_ep_res?.toString(),
            "Total épisodes" to roman.nb_ep_tot?.toString(),
            "Mis à jour" to roman.updated,
            "Notice" to roman.notice
        )
        DetailDialog(
            title = roman.titre ?: "Détails du roman",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedRoman = null }
        )
    }
}
