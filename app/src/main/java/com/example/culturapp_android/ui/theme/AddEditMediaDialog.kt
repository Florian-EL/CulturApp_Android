package com.example.culturapp_android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.culturapp_android.data.Film
import com.example.culturapp_android.data.Manga
import com.example.culturapp_android.data.Roman
import com.example.culturapp_android.data.Serie
import com.example.culturapp_android.data.Wattpad
import com.example.culturapp_android.data.Webtoon

private fun String?.sanitizeString(): String? {
    val trimmed = (this ?: "").trim()
    return if (trimmed.isEmpty() || trimmed.equals("None", ignoreCase = true) || trimmed.equals("null", ignoreCase = true) || trimmed.equals("0", ignoreCase = true)) null else trimmed
}

@Composable
fun NumberCounter(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            textAlign = TextAlign.Center
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    onValueChange((value - 1).coerceAtLeast(minValue))
                },
                enabled = value > minValue
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Diminuer"
                )
            }

            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(
                onClick = {
                    onValueChange((value + 1).coerceAtMost(maxValue))
                },
                enabled = value < maxValue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Augmenter"
                )
            }
        }
    }
}
@Composable
fun AddEditFilmDialog(
    film: Film? = null,
    onDismiss: () -> Unit,
    onSave: (Film) -> Unit
) {
    var titrePrincipal by remember { mutableStateOf(film?.titre_principal ?: "") }
    var titreSecondaire by remember { mutableStateOf(film?.titre_secondaire ?: "") }
    var note by remember { mutableStateOf(film?.note?.toString() ?: "") }
    var type by remember { mutableStateOf(film?.type ?: "") }
    var genre by remember { mutableStateOf(film?.genre ?: "") }
    var vo by remember { mutableStateOf(film?.vo ?: "") }
    var cinema by remember { mutableStateOf(film?.cinema?.toString() ?: "") }
    var updated by remember { mutableStateOf(film?.updated ?: "") }
    var anneeVu by remember { mutableStateOf(film?.annee_vu?.toString() ?: "") }
    var sortie by remember { mutableStateOf(film?.sortie ?: "") }
    var notice by remember { mutableStateOf(film?.notice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (film == null) "Ajouter un Film" else "Modifier le Film",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titrePrincipal,
                    onValueChange = { titrePrincipal = it },
                    label = { Text("Titre principal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titreSecondaire,
                    onValueChange = { titreSecondaire = it },
                    label = { Text("Titre secondaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (/20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = vo,
                    onValueChange = { vo = it },
                    label = { Text("VO / Langue") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = anneeVu,
                    onValueChange = { anneeVu = it },
                    label = { Text("Année vu") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sortie,
                    onValueChange = { sortie = it },
                    label = { Text("Sortie") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updated,
                    onValueChange = { updated = it },
                    label = { Text("Statut sortie (ex: PAS SORTI)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Notice / Commentaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Button(
                        onClick = {
                            val newFilm = Film(
                                id = film?.id,
                                titre = film?.titre,
                                note = note.toIntOrNull()?.takeIf { it > 0 },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                vo = vo.sanitizeString(),
                                cinema = cinema.toIntOrNull()?.takeIf { it > 0 },
                                updated = updated.sanitizeString(),
                                etat = null,
                                annee_vu = anneeVu.toIntOrNull()?.takeIf { it > 0 },
                                nb_vu = null,
                                sortie = sortie.sanitizeString(),
                                nb_ep_vu = null,
                                nb_ep_res = null,
                                nb_ep_tot = null,
                                notice = notice.sanitizeString(),
                                titre_principal = titrePrincipal.sanitizeString(),
                                titre_secondaire = titreSecondaire.sanitizeString()
                            )
                            onSave(newFilm)
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditMangaDialog(
    manga: Manga? = null,
    onDismiss: () -> Unit,
    onSave: (Manga) -> Unit
) {
    var titrePrincipal by remember { mutableStateOf(manga?.titre_principal ?: "") }
    var titreSecondaire by remember { mutableStateOf(manga?.titre_secondaire ?: "") }
    var auteur by remember { mutableStateOf(manga?.auteur ?: "") }
    var note by remember { mutableStateOf(manga?.note?.toString() ?: "") }
    var type by remember { mutableStateOf(manga?.type ?: "") }
    var genre by remember { mutableStateOf(manga?.genre ?: "") }
    var vo by remember { mutableStateOf(manga?.vo ?: "") }
    var epDeb by remember { mutableStateOf(manga?.ep_deb?.toString() ?: "") }
    var epAct by remember { mutableStateOf(manga?.ep_act?.toString() ?: "") }
    var nbEpTot by remember { mutableStateOf(manga?.nb_ep_tot?.toString() ?: "") }
    var updated by remember { mutableStateOf(manga?.updated ?: "") }
    var site by remember { mutableStateOf(manga?.site ?: "") }
    var luSuite by remember { mutableStateOf(manga?.lu_suite ?: "") }
    var notice by remember { mutableStateOf(manga?.notice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (manga == null) "Ajouter un Manga" else "Modifier le Manga",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titrePrincipal,
                    onValueChange = { titrePrincipal = it },
                    label = { Text("Titre principal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titreSecondaire,
                    onValueChange = { titreSecondaire = it },
                    label = { Text("Titre secondaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = auteur,
                    onValueChange = { auteur = it },
                    label = { Text("Auteur") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (/20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = epDeb,
                    onValueChange = { epDeb = it },
                    label = { Text("Chapitre / Épisode Début") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = epAct,
                    onValueChange = { epAct = it },
                    label = { Text("Chapitre / Épisode Actuel") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpTot,
                    onValueChange = { nbEpTot = it },
                    label = { Text("Nombre total d'épisodes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = site,
                    onValueChange = { site = it },
                    label = { Text("Site / Plateforme") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updated,
                    onValueChange = { updated = it },
                    label = { Text("Statut mise à jour") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Notice") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Button(
                        onClick = {
                            val newManga = Manga(
                                id = manga?.id,
                                titre = manga?.titre,
                                auteur = auteur.sanitizeString(),
                                note = note.toIntOrNull()?.takeIf { it > 0 },
                                type = type.sanitizeString(),
                                vo = vo.sanitizeString(),
                                genre = genre.sanitizeString(),
                                lu_suite = luSuite.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = null,
                                ep_deb = epDeb.toIntOrNull()?.takeIf { it > 0 },
                                ep_act = epAct.toIntOrNull()?.takeIf { it > 0 },
                                nb_ep_res = null,
                                site = site.sanitizeString(),
                                nb_ep_tot = nbEpTot.toIntOrNull()?.takeIf { it > 0 },
                                nb_ep_vu = null,
                                notice = notice.sanitizeString(),
                                titre_principal = titrePrincipal.sanitizeString(),
                                titre_secondaire = titreSecondaire.sanitizeString()
                            )
                            onSave(newManga)
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditSerieDialog(
    serie: Serie? = null,
    onDismiss: () -> Unit,
    onSave: (Serie) -> Unit
) {
    var titrePrincipal by remember { mutableStateOf(serie?.titre_principal ?: "") }
    var titreSecondaire by remember { mutableStateOf(serie?.titre_secondaire ?: "") }
    var note by remember { mutableStateOf(serie?.note?.toString() ?: "") }
    var sortie by remember { mutableStateOf(serie?.sortie ?: "") }
    var priorite by remember { mutableIntStateOf(serie?.priorite ?: 0) }
    var type by remember { mutableStateOf(serie?.type ?: "") }
    var genre by remember { mutableStateOf(serie?.genre ?: "") }
    var vo by remember { mutableStateOf(serie?.vo ?: "") }
    var nbEpVu by remember { mutableIntStateOf(serie?.nb_ep_vu ?: 0) }
    var nbEpTot by remember { mutableIntStateOf(serie?.nb_ep_tot ?: 0) }
    var updated by remember { mutableStateOf(serie?.updated ?: "") }
    var studio by remember { mutableStateOf(serie?.studio ?: "") }
    var notice by remember { mutableStateOf(serie?.notice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)) {
                Text(
                    text = if (serie == null) "Ajouter une Série" else "Modifier $titrePrincipal",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,

                    )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titrePrincipal,
                    onValueChange = { titrePrincipal = it },
                    label = { Text("Titre principal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titreSecondaire,
                    onValueChange = { titreSecondaire = it },
                    label = { Text("Titre secondaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (/20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sortie,
                    onValueChange = { sortie = it },
                    label = { Text("Date de sortie") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                NumberCounter(
                    label = "Priorité",
                    value = priorite,
                    onValueChange = { priorite = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberCounter(
                        label = "Épisodes vus",
                        value = nbEpVu,
                        onValueChange = { nbEpVu = it },
                        maxValue = nbEpTot,
                        modifier = Modifier.weight(1f)
                    )

                    NumberCounter(
                        label = "Épisodes total",
                        value = nbEpTot,
                        onValueChange = {
                            nbEpTot = it
                            nbEpVu = nbEpVu.coerceAtMost(it)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("Statut mise à jour")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("PAS SORTI", "EN COURS", "FINI").forEach { status ->
                            OutlinedButton(
                                onClick = { updated = status },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(
                                    horizontal = 4.dp,
                                    vertical = 4.dp
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (updated == status)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        Color.Transparent
                                )
                            ) {
                                Text(
                                    status,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = studio,
                    onValueChange = { studio = it },
                    label = { Text("Studio") },
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Notice") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Button(
                        onClick = {
                            val newSerie = Serie(
                                id = serie?.id,
                                titre = serie?.titre,
                                note = note.toIntOrNull()?.takeIf { it > 0 },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                vo = vo.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                sortie = sortie,
                                nb_vu = serie?.nb_vu,
                                nb_ep_vu = nbEpVu,
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot,
                                priorite = priorite.takeIf { it > 0 },
                                notice = notice.sanitizeString(),
                                titre_principal = titrePrincipal.sanitizeString(),
                                titre_secondaire = titreSecondaire.sanitizeString(),
                                studio = studio.sanitizeString()
                            )
                            onSave(newSerie)
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditRomanDialog(
    roman: Roman? = null,
    onDismiss: () -> Unit,
    onSave: (Roman) -> Unit
) {
    var titrePrincipal by remember { mutableStateOf(roman?.titre_principal ?: "") }
    var titreSecondaire by remember { mutableStateOf(roman?.titre_secondaire ?: "") }
    var auteur by remember { mutableStateOf(roman?.auteur ?: "") }
    var note by remember { mutableStateOf(roman?.note?.toString() ?: "") }
    var type by remember { mutableStateOf(roman?.type ?: "") }
    var genre by remember { mutableStateOf(roman?.genre ?: "") }
    var nbEpVu by remember { mutableStateOf(roman?.nb_ep_vu?.toString() ?: "") }
    var nbEpTot by remember { mutableStateOf(roman?.nb_ep_tot?.toString() ?: "") }
    var updated by remember { mutableStateOf(roman?.updated ?: "") }
    var possede by remember { mutableStateOf(roman?.possede ?: "") }
    var notice by remember { mutableStateOf(roman?.notice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (roman == null) "Ajouter un Roman" else "Modifier le Roman",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titrePrincipal,
                    onValueChange = { titrePrincipal = it },
                    label = { Text("Titre principal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titreSecondaire,
                    onValueChange = { titreSecondaire = it },
                    label = { Text("Titre secondaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = auteur,
                    onValueChange = { auteur = it },
                    label = { Text("Auteur") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (/20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpVu,
                    onValueChange = { nbEpVu = it },
                    label = { Text("Chapitres/Tomes lus") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpTot,
                    onValueChange = { nbEpTot = it },
                    label = { Text("Total Chapitres/Tomes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = possede,
                    onValueChange = { possede = it },
                    label = { Text("Possédé") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updated,
                    onValueChange = { updated = it },
                    label = { Text("Statut mise à jour") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Notice") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Button(
                        onClick = {
                            val newRoman = Roman(
                                id = roman?.id,
                                titre = roman?.titre,
                                auteur = auteur.sanitizeString(),
                                note = note.toIntOrNull()?.takeIf { it > 0 },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = roman?.nb_vu,
                                nb_ep_vu = nbEpVu.toIntOrNull()?.takeIf { it > 0 },
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot.toIntOrNull()?.takeIf { it > 0 },
                                possede = possede.sanitizeString(),
                                notice = notice.sanitizeString(),
                                titre_principal = titrePrincipal.sanitizeString(),
                                titre_secondaire = titreSecondaire.sanitizeString()
                            )
                            onSave(newRoman)
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditWebtoonDialog(
    webtoon: Webtoon? = null,
    onDismiss: () -> Unit,
    onSave: (Webtoon) -> Unit
) {
    var titrePrincipal by remember { mutableStateOf(webtoon?.titre_principal ?: "") }
    var titreSecondaire by remember { mutableStateOf(webtoon?.titre_secondaire ?: "") }
    var auteur by remember { mutableStateOf(webtoon?.auteur ?: "") }
    var note by remember { mutableStateOf(webtoon?.note?.toString() ?: "") }
    var type by remember { mutableStateOf(webtoon?.type ?: "") }
    var genre by remember { mutableStateOf(webtoon?.genre ?: "") }
    var nbEpVu by remember { mutableStateOf(webtoon?.nb_ep_vu?.toString() ?: "") }
    var nbEpTot by remember { mutableStateOf(webtoon?.nb_ep_tot?.toString() ?: "") }
    var updated by remember { mutableStateOf(webtoon?.updated ?: "") }
    var notice by remember { mutableStateOf(webtoon?.notice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (webtoon == null) "Ajouter un Webtoon" else "Modifier le Webtoon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titrePrincipal,
                    onValueChange = { titrePrincipal = it },
                    label = { Text("Titre principal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titreSecondaire,
                    onValueChange = { titreSecondaire = it },
                    label = { Text("Titre secondaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = auteur,
                    onValueChange = { auteur = it },
                    label = { Text("Auteur") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (/20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpVu,
                    onValueChange = { nbEpVu = it },
                    label = { Text("Épisodes vus") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpTot,
                    onValueChange = { nbEpTot = it },
                    label = { Text("Total Épisodes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updated,
                    onValueChange = { updated = it },
                    label = { Text("Statut mise à jour") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Notice") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Button(
                        onClick = {
                            val newWebtoon = Webtoon(
                                id = webtoon?.id,
                                titre = webtoon?.titre,
                                auteur = auteur.sanitizeString(),
                                note = note.toIntOrNull()?.takeIf { it > 0 },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = webtoon?.nb_vu,
                                nb_ep_vu = nbEpVu.toIntOrNull()?.takeIf { it > 0 },
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot.toIntOrNull()?.takeIf { it > 0 },
                                notice = notice.sanitizeString(),
                                titre_principal = titrePrincipal.sanitizeString(),
                                titre_secondaire = titreSecondaire.sanitizeString()
                            )
                            onSave(newWebtoon)
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditWattpadDialog(
    wattpad: Wattpad? = null,
    onDismiss: () -> Unit,
    onSave: (Wattpad) -> Unit
) {
    var titrePrincipal by remember { mutableStateOf(wattpad?.titre_principal ?: "") }
    var titreSecondaire by remember { mutableStateOf(wattpad?.titre_secondaire ?: "") }
    var auteur by remember { mutableStateOf(wattpad?.auteur ?: "") }
    var note by remember { mutableStateOf(wattpad?.note?.toString() ?: "") }
    var type by remember { mutableStateOf(wattpad?.type ?: "") }
    var genre by remember { mutableStateOf(wattpad?.genre ?: "") }
    var nbEpVu by remember { mutableStateOf(wattpad?.nb_ep_vu?.toString() ?: "") }
    var nbEpTot by remember { mutableStateOf(wattpad?.nb_ep_tot?.toString() ?: "") }
    var updated by remember { mutableStateOf(wattpad?.updated ?: "") }
    var notice by remember { mutableStateOf(wattpad?.notice ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (wattpad == null) "Ajouter sur Wattpad" else "Modifier sur Wattpad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titrePrincipal,
                    onValueChange = { titrePrincipal = it },
                    label = { Text("Titre principal") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = titreSecondaire,
                    onValueChange = { titreSecondaire = it },
                    label = { Text("Titre secondaire") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = auteur,
                    onValueChange = { auteur = it },
                    label = { Text("Auteur") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (/20)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpVu,
                    onValueChange = { nbEpVu = it },
                    label = { Text("Chapitres vus") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nbEpTot,
                    onValueChange = { nbEpTot = it },
                    label = { Text("Total Chapitres") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = updated,
                    onValueChange = { updated = it },
                    label = { Text("Statut mise à jour") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notice,
                    onValueChange = { notice = it },
                    label = { Text("Notice") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Button(
                        onClick = {
                            val newWattpad = Wattpad(
                                id = wattpad?.id,
                                titre = wattpad?.titre,
                                auteur = auteur.sanitizeString(),
                                note = note.toIntOrNull()?.takeIf { it > 0 },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = wattpad?.nb_vu,
                                nb_ep_vu = nbEpVu.toIntOrNull()?.takeIf { it > 0 },
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot.toIntOrNull()?.takeIf { it > 0 },
                                notice = notice.sanitizeString(),
                                titre_principal = titrePrincipal.sanitizeString(),
                                titre_secondaire = titreSecondaire.sanitizeString()
                            )
                            onSave(newWattpad)
                        }
                    ) {
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
