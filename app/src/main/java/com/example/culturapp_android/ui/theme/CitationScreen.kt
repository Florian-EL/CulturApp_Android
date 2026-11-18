package com.example.culturapp_android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.culturapp_android.data.Citation
import com.example.culturapp_android.data.Film
import com.example.culturapp_android.data.Manga
import com.example.culturapp_android.data.Roman
import com.example.culturapp_android.data.Serie
import com.example.culturapp_android.data.Wattpad
import com.example.culturapp_android.data.Webtoon
import java.io.File

@Composable
fun CitationScreen(
    citations: List<Citation>,
    films: List<Film> = emptyList(),
    series: List<Serie> = emptyList(),
    romans: List<Roman> = emptyList(),
    mangas: List<Manga> = emptyList(),
    webtoons: List<Webtoon> = emptyList(),
    wattpads: List<Wattpad> = emptyList(),
    imagesVersion: Int = 0,
    onSaveCitation: ((Citation) -> Unit)? = null,
    onDeleteCitation: ((Citation) -> Unit)? = null
) {
    var selectedCitation by remember { mutableStateOf<Citation?>(null) }
    var editingCitation by remember { mutableStateOf<Citation?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Citations (${citations.size})",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (citations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune citation enregistrée",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(citations) { citationItem ->
                        val coverFile = remember(
                            citationItem.id,
                            citationItem.oeuvre,
                            imagesVersion,
                            films,
                            series,
                            romans,
                            mangas,
                            webtoons,
                            wattpads
                        ) {
                            findCoverImageForOeuvre(
                                context = context,
                                oeuvre = citationItem.oeuvre,
                                films = films,
                                series = series,
                                romans = romans,
                                mangas = mangas,
                                webtoons = webtoons,
                                wattpads = wattpads
                            )
                        }

                        CitationRowCard(
                            citation = citationItem,
                            coverFile = coverFile,
                            onClick = { selectedCitation = citationItem }
                        )
                    }
                }
            }
        }

        if (onSaveCitation != null) {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter une citation")
            }
        }
    }

    if (showAddDialog && onSaveCitation != null) {
        AddEditCitationDialog(
            citation = null,
            onDismiss = { showAddDialog = false },
            onSave = { newCitation ->
                onSaveCitation(newCitation)
                showAddDialog = false
            }
        )
    }

    editingCitation?.let { citationToEdit ->
        if (onSaveCitation != null) {
            AddEditCitationDialog(
                citation = citationToEdit,
                onDismiss = { editingCitation = null },
                onSave = { updatedCitation ->
                    onSaveCitation(updatedCitation)
                    editingCitation = null
                    selectedCitation = null
                }
            )
        }
    }

    selectedCitation?.let { citationItem ->
        val coverFile = findCoverImageForOeuvre(
            context = context,
            oeuvre = citationItem.oeuvre,
            films = films,
            series = series,
            romans = romans,
            mangas = mangas,
            webtoons = webtoons,
            wattpads = wattpads
        )
        val details = listOf(
            "Citation" to citationItem.citation,
            "Œuvre" to citationItem.oeuvre,
            "Artiste" to citationItem.artiste,
            "Personnage" to citationItem.personnage
        )
        DetailDialog(
            title = "Détails de la citation",
            details = details,
            coverFile = coverFile,
            onDismiss = { selectedCitation = null },
            onEdit = if (onSaveCitation != null) {
                { editingCitation = citationItem }
            } else null,
            onDelete = if (onDeleteCitation != null) {
                {
                    onDeleteCitation(citationItem)
                    selectedCitation = null
                }
            } else null
        )
    }
}

@Composable
fun CitationRowCard(
    citation: Citation,
    coverFile: File?,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tout à gauche, l'image de l'oeuvre en question si existante
            Box(
                modifier = Modifier
                    .size(70.dp, 95.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (coverFile != null && coverFile.exists()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(coverFile)
                            .crossfade(true)
                            .build(),
                        contentDescription = citation.oeuvre,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Colonne de droite : citation en taille assez importante, en dessous oeuvre/artiste, en dessous personnage si présent
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Citation en taille assez importante
                Text(
                    text = citation.citation?.let { "“$it”" } ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Oeuvre ou Artiste
                val workOrArtist = listOfNotNull(
                    citation.oeuvre?.takeIf { it.isNotBlank() },
                    citation.artiste?.takeIf { it.isNotBlank() }
                ).joinToString(" — ")

                if (workOrArtist.isNotBlank()) {
                    Text(
                        text = workOrArtist,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Personnage si présent
                if (!citation.personnage.isNullOrBlank()) {
                    Text(
                        text = "Personnage : ${citation.personnage}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditCitationDialog(
    citation: Citation?,
    onDismiss: () -> Unit,
    onSave: (Citation) -> Unit
) {
    var citationText by remember { mutableStateOf(citation?.citation ?: "") }
    var oeuvreText by remember { mutableStateOf(citation?.oeuvre ?: "") }
    var artisteText by remember { mutableStateOf(citation?.artiste ?: "") }
    var personnageText by remember { mutableStateOf(citation?.personnage ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (citation == null) "Ajouter une citation" else "Modifier la citation",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                OutlinedTextField(
                    value = citationText,
                    onValueChange = { citationText = it },
                    label = { Text("Citation") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                OutlinedTextField(
                    value = oeuvreText,
                    onValueChange = { oeuvreText = it },
                    label = { Text("Œuvre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = artisteText,
                    onValueChange = { artisteText = it },
                    label = { Text("Artiste") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = personnageText,
                    onValueChange = { personnageText = it },
                    label = { Text("Personnage") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.Button(
                        onClick = {
                            onSave(
                                Citation(
                                    id = citation?.id,
                                    citation = citationText.takeIf { it.isNotBlank() },
                                    oeuvre = oeuvreText.takeIf { it.isNotBlank() },
                                    artiste = artisteText.takeIf { it.isNotBlank() },
                                    personnage = personnageText.takeIf { it.isNotBlank() }
                                )
                            )
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
