package com.example.culturapp_android.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

val TYPE_OPTIONS = listOf(
    "ANIME", "S_F_ANIME", "S_ANIME", "P_MANGA", "MANGA", "WEBTOON", "WATTPAD",
    "DOCUMENTAIRE", "S_FILM", "S_DOCUMENTAIRE", "P_MANHWA", "MANHWA", "NOCTELLER",
    "FILM", "S_F_DOCUMENTAIRE", "S_SERIE", "P_ROMAN", "ROMAN", "S_F_CONCERT", "S_KDRAMA",
    "P_BD", "MANHUA", "P_ROMAN-G", "P_MANHUA"
)

val GENRE_OPTIONS = listOf(
    "BL", "Comedy", "Animation", "Romance", "SF", "Sport", "Horreur", "Guerre",
    "Historique", "Fantastique", "Voyage", "Réalisme", "Musique", "Slice of Life",
    "Espionnage", "Art", "Action", "Policier", "Art martiaux", "Post apocalyptique"
)

val VO_OPTIONS = listOf(
    "FRA", "ENG", "JAP", "CHI", "KOR", "NVG", "THAI", "IND"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = onValueChange,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        val filteredOptions = remember(selectedValue, options) {
            if (selectedValue.isBlank()) options
            else {
                val filtered = options.filter { it.contains(selectedValue, ignoreCase = true) }
                if (filtered.isNotEmpty()) filtered else options
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiYearSelector(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    startYear: Int = 2000,
    endYear: Int = java.time.LocalDate.now().year
) {
    val selectedYears = remember(value) {
        value.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
    }

    val years = (endYear downTo startYear).toList()

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Année vu",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (selectedYears.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                val indexedYears = selectedYears.mapIndexed { index, year -> index to year }
                    .sortedByDescending { it.second }

                for ((index, year) in indexedYears) {
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(year.toString()) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Supprimer $year",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable {
                                        val mutable = selectedYears.toMutableList()
                                        mutable.removeAt(index)
                                        onValueChange(mutable.sortedDescending().joinToString(", "))
                                    }
                            )
                        }
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(years) { year ->
                val count = selectedYears.count { it == year }
                val selected = count > 0

                val backgroundColor = if (selected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant

                val textColor = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = backgroundColor,
                    modifier = Modifier
                        .clickable {
                            val newYears = (selectedYears + year).sortedDescending()
                            onValueChange(newYears.joinToString(", "))
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = year.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                        if (count > 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
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
    var note by remember { mutableFloatStateOf(film?.note ?: 0f) }
    var type by remember { mutableStateOf(film?.type ?: "") }
    var genre by remember { mutableStateOf(film?.genre ?: "") }
    var vo by remember { mutableStateOf(film?.vo ?: "") }
    var cinema by remember { mutableStateOf(film?.cinema ?: "") }
    var nbEpVu by remember { mutableIntStateOf(film?.nb_ep_vu ?: 0) }
    var nbEpTot by remember { mutableIntStateOf(film?.nb_ep_tot ?: 0) }
    var updated by remember { mutableStateOf(film?.updated ?: "") }
    var anneeVu by remember { mutableStateOf(film?.annee_vu ?: "") }
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
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = if (film == null) "Ajouter un Film" else "Modifier le Film",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
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

                    Column {
                        Text("Note : ${note}/20")

                        Slider(
                            value = note,
                            onValueChange = { note = it },
                            valueRange = 0f..20f,
                            steps = 79
                        )
                    }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Type",
                        selectedValue = type,
                        options = TYPE_OPTIONS,
                        onValueChange = { type = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Genre",
                        selectedValue = genre,
                        options = GENRE_OPTIONS,
                        onValueChange = { genre = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "VO / Langue",
                        selectedValue = vo,
                        options = VO_OPTIONS,
                        onValueChange = { vo = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MultiYearSelector(
                        value = anneeVu,
                        onValueChange = { anneeVu = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = sortie,
                        onValueChange = { sortie = it },
                        label = { Text("Sortie") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Text("Cinéma")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("OUI", "NON").forEach { option ->
                                OutlinedButton(
                                    onClick = { cinema = option },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(
                                        horizontal = 4.dp,
                                        vertical = 4.dp
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (cinema == option)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            Color.Transparent
                                    )
                                ) {
                                    Text(
                                        option,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

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
                        value = notice,
                        onValueChange = { notice = it },
                        label = { Text("Notice / Commentaire") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                                note = note.takeIf { it > 0f },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                vo = vo.sanitizeString(),
                                cinema = cinema,
                                updated = updated.sanitizeString(),
                                etat = null,
                                annee_vu = anneeVu.sanitizeString(),
                                nb_vu = null,
                                sortie = sortie.sanitizeString(),
                                nb_ep_vu = nbEpVu,
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot,
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
    var note by remember { mutableFloatStateOf(manga?.note ?: 0f) }
    var type by remember { mutableStateOf(manga?.type ?: "") }
    var genre by remember { mutableStateOf(manga?.genre ?: "") }
    var vo by remember { mutableStateOf(manga?.vo ?: "") }
    var epDeb by remember { mutableStateOf(manga?.ep_deb?.toString() ?: "") }
    var nbEpVu by remember { mutableIntStateOf(manga?.nb_ep_vu ?: manga?.ep_act ?: 0) }
    var nbEpTot by remember { mutableIntStateOf(manga?.nb_ep_tot ?: 0) }
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
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = if (manga == null) "Ajouter un Manga" else "Modifier le Manga",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
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

                    Column {
                        Text("Note : ${note}/20")

                        Slider(
                            value = note,
                            onValueChange = { note = it },
                            valueRange = 0f..20f,
                            steps = 79
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = epDeb,
                        onValueChange = { epDeb = it },
                        label = { Text("Chapitre / Épisode Début") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Type",
                        selectedValue = type,
                        options = TYPE_OPTIONS,
                        onValueChange = { type = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Genre",
                        selectedValue = genre,
                        options = GENRE_OPTIONS,
                        onValueChange = { genre = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "VO / Langue",
                        selectedValue = vo,
                        options = VO_OPTIONS,
                        onValueChange = { vo = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = site,
                        onValueChange = { site = it },
                        label = { Text("Site / Plateforme") },
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
                        value = notice,
                        onValueChange = { notice = it },
                        label = { Text("Notice") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                                note = note.takeIf { it > 0f },
                                type = type.sanitizeString(),
                                vo = vo.sanitizeString(),
                                genre = genre.sanitizeString(),
                                lu_suite = luSuite.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = null,
                                ep_deb = epDeb.toIntOrNull()?.takeIf { it > 0 },
                                ep_act = epDeb.toIntOrNull()?.let { it + nbEpVu } ?: nbEpVu,
                                nb_ep_res = null,
                                site = site.sanitizeString(),
                                nb_ep_tot = nbEpTot,
                                nb_ep_vu = nbEpVu,
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
    var note by remember { mutableFloatStateOf(serie?.note ?: 0f) }
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
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = if (serie == null) "Ajouter une Série" else "Modifier la Série",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
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

                    Column {
                        Text("Note : ${note}/20")

                        Slider(
                            value = note,
                            onValueChange = { note = it },
                            valueRange = 0f..20f,
                            steps = 79
                        )
                    }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Type",
                        selectedValue = type,
                        options = TYPE_OPTIONS,
                        onValueChange = { type = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Genre",
                        selectedValue = genre,
                        options = GENRE_OPTIONS,
                        onValueChange = { genre = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "VO / Langue",
                        selectedValue = vo,
                        options = VO_OPTIONS,
                        onValueChange = { vo = it }
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
                }

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
                                note = note.takeIf { it > 0f },
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
    var note by remember { mutableFloatStateOf(roman?.note ?: 0f) }
    var type by remember { mutableStateOf(roman?.type ?: "") }
    var genre by remember { mutableStateOf(roman?.genre ?: "") }
    var nbEpVu by remember { mutableIntStateOf(roman?.nb_ep_vu ?: 0) }
    var nbEpTot by remember { mutableIntStateOf(roman?.nb_ep_tot ?: 0) }
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
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = if (roman == null) "Ajouter un Roman" else "Modifier le Roman",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
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

                    Column {
                        Text("Note : ${note}/20")

                        Slider(
                            value = note,
                            onValueChange = { note = it },
                            valueRange = 0f..20f,
                            steps = 79
                        )
                    }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Type",
                        selectedValue = type,
                        options = TYPE_OPTIONS,
                        onValueChange = { type = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Genre",
                        selectedValue = genre,
                        options = GENRE_OPTIONS,
                        onValueChange = { genre = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Text("Possédé")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("OUI", "NON").forEach { option ->
                                OutlinedButton(
                                    onClick = { possede = option },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(
                                        horizontal = 4.dp,
                                        vertical = 4.dp
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (possede == option)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            Color.Transparent
                                    )
                                ) {
                                    Text(
                                        option,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

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
                        value = notice,
                        onValueChange = { notice = it },
                        label = { Text("Notice") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                                note = note.takeIf { it > 0f },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = roman?.nb_vu,
                                nb_ep_vu = nbEpVu,
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot,
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
    var note by remember { mutableFloatStateOf(webtoon?.note ?: 0f) }
    var type by remember { mutableStateOf(webtoon?.type ?: "") }
    var genre by remember { mutableStateOf(webtoon?.genre ?: "") }
    var nbEpVu by remember { mutableIntStateOf(webtoon?.nb_ep_vu ?: 0) }
    var nbEpTot by remember { mutableIntStateOf(webtoon?.nb_ep_tot ?: 0) }
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
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = if (webtoon == null) "Ajouter un Webtoon" else "Modifier le Webtoon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
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

                    Column {
                        Text("Note : ${note}/20")

                        Slider(
                            value = note,
                            onValueChange = { note = it },
                            valueRange = 0f..20f,
                            steps = 79
                        )
                    }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Type",
                        selectedValue = type,
                        options = TYPE_OPTIONS,
                        onValueChange = { type = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Genre",
                        selectedValue = genre,
                        options = GENRE_OPTIONS,
                        onValueChange = { genre = it }
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
                        value = notice,
                        onValueChange = { notice = it },
                        label = { Text("Notice") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                                note = note.takeIf { it > 0f },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = webtoon?.nb_vu,
                                nb_ep_vu = nbEpVu,
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot,
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
    var note by remember { mutableFloatStateOf(wattpad?.note ?: 0f) }
    var type by remember { mutableStateOf(wattpad?.type ?: "") }
    var genre by remember { mutableStateOf(wattpad?.genre ?: "") }
    var nbEpVu by remember { mutableIntStateOf(wattpad?.nb_ep_vu ?: 0) }
    var nbEpTot by remember { mutableIntStateOf(wattpad?.nb_ep_tot ?: 0) }
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
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = if (wattpad == null) "Ajouter sur Wattpad" else "Modifier sur Wattpad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
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

                    Column {
                        Text("Note : ${note}/20")

                        Slider(
                            value = note,
                            onValueChange = { note = it },
                            valueRange = 0f..20f,
                            steps = 79
                        )
                    }

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

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Type",
                        selectedValue = type,
                        options = TYPE_OPTIONS,
                        onValueChange = { type = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DropdownMenuField(
                        label = "Genre",
                        selectedValue = genre,
                        options = GENRE_OPTIONS,
                        onValueChange = { genre = it }
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
                        value = notice,
                        onValueChange = { notice = it },
                        label = { Text("Notice") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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
                                note = note.takeIf { it > 0f },
                                type = type.sanitizeString(),
                                genre = genre.sanitizeString(),
                                updated = updated.sanitizeString(),
                                etat = null,
                                nb_vu = wattpad?.nb_vu,
                                nb_ep_vu = nbEpVu,
                                nb_ep_res = null,
                                nb_ep_tot = nbEpTot,
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
