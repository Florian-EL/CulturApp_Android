package com.example.culturapp_android.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.culturapp_android.AppScreen
import com.example.culturapp_android.data.Citation
import com.example.culturapp_android.data.Film
import com.example.culturapp_android.data.Manga
import com.example.culturapp_android.data.Roman
import com.example.culturapp_android.data.Serie
import com.example.culturapp_android.data.Wattpad
import com.example.culturapp_android.data.Webtoon
import java.util.Locale

data class TypeStat(
    val title: String,
    val count: Int,
    val seenEp: Int,
    val totalEp: Int,
    val progress: Float,
    val avgNote: Float?,
    val icon: ImageVector,
    val accentColor: Color,
    val screen: AppScreen,
)

@Composable
fun EntranceScreen(
    films: List<Film>,
    series: List<Serie>,
    romans: List<Roman>,
    mangas: List<Manga>,
    webtoon: List<Webtoon>,
    wattpad: List<Wattpad>,
    citations: List<Citation> = emptyList(),
    imagesVersion: Int = 0,
    onNavigateToScreen: (AppScreen) -> Unit = {}
) {
    val context = LocalContext.current
    var currentCitation by remember(citations) {
        mutableStateOf(if (citations.isNotEmpty()) citations.shuffled().firstOrNull() else null)
    }

    val stats = remember(films, series, romans, mangas, webtoon, wattpad) {
        listOf(
            createStat(
                "Films",
                films,
                films.map { f -> f.nb_ep_vu },
                films.map { f -> f.nb_ep_tot },
                films.map { f -> f.note },
                films.map { f -> f.etat },
                Icons.Default.Movie,
                Color(0xFFE53935),
                AppScreen.FILMS
            ),
            createStat(
                "Séries",
                series,
                series.map { s -> s.nb_ep_vu },
                series.map { s -> s.nb_ep_tot },
                series.map { s -> s.note },
                series.map { s -> s.etat },
                Icons.Default.Tv,
                Color(0xFF3F51B5),
                AppScreen.SERIES
            ),
            createStat(
                "Romans",
                romans,
                romans.map { r -> r.nb_ep_vu },
                romans.map { r -> r.nb_ep_tot },
                romans.map { r -> r.note },
                romans.map { r -> r.etat },
                Icons.Default.Book,
                Color(0xFFFF9800),
                AppScreen.ROMAN
            ),
            createStat(
                "Mangas",
                mangas,
                mangas.map { m -> m.nb_ep_vu },
                mangas.map { m -> m.nb_ep_tot },
                mangas.map { m -> m.note },
                mangas.map { m -> m.etat },
                Icons.AutoMirrored.Filled.MenuBook,
                Color(0xFF4CAF50),
                AppScreen.MANGA
            ),
            createStat(
                "Webtoons",
                webtoon,
                webtoon.map { w -> w.nb_ep_vu },
                webtoon.map { w -> w.nb_ep_tot },
                webtoon.map { w -> w.note },
                webtoon.map { w -> w.etat },
                Icons.Default.AutoStories,
                Color(0xFF00BCD4),
                AppScreen.WEBTOON
            ),
            createStat(
                "Wattpad",
                wattpad,
                wattpad.map { wt -> wt.nb_ep_vu },
                wattpad.map { wt -> wt.nb_ep_tot },
                wattpad.map { wt -> wt.note },
                wattpad.map { wt -> wt.etat },
                Icons.Default.Bookmark,
                Color(0xFFE91E63),
                AppScreen.WATTPAD
            )
        )
    }

    val totalMediaCount = remember(stats) { stats.sumOf { it.count } }
    val totalSeenEp = remember(stats) { stats.sumOf { it.seenEp } }
    val globalAvgNote = remember(films, series, romans, mangas, webtoon, wattpad) {
        val allNotes = (films.mapNotNull { it.note } +
                series.mapNotNull { it.note } +
                romans.mapNotNull { it.note } +
                mangas.mapNotNull { it.note } +
                webtoon.mapNotNull { it.note } +
                wattpad.mapNotNull { it.note }).filter { it > 0 }
        if (allNotes.isNotEmpty()) allNotes.average().toFloat() else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Top Global Summary Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Aperçu Général",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GlobalStatBadge(
                        label = "Titres",
                        value = totalMediaCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    GlobalStatBadge(
                        label = "Épisodes/Chap. vus",
                        value = totalSeenEp.toString(),
                        modifier = Modifier.weight(1.2f)
                    )
                    GlobalStatBadge(
                        label = "Note moyenne",
                        value = if (globalAvgNote != null) String.format(
                            Locale.FRANCE,
                            "%.1f/20",
                            globalAvgNote
                        ) else "-",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Random Citation Card at bottom
        currentCitation?.let { citation ->
            val coverFile = remember(citation.id, citation.oeuvre, imagesVersion) {
                findCoverImageForOeuvre(
                    context = context,
                    oeuvre = citation.oeuvre,
                    films = films,
                    series = series,
                    romans = romans,
                    mangas = mangas,
                    webtoons = webtoon,
                    wattpads = wattpad
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScreen(AppScreen.CITATIONS) },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image tout à gauche
                    Box(
                        modifier = Modifier
                            .size(45.dp, 60.dp)
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
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = citation.citation?.let { "“$it”" } ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Italic,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val workOrArtist = listOfNotNull(
                            citation.oeuvre?.takeIf { it.isNotBlank() },
                            citation.artiste?.takeIf { it.isNotBlank() }
                        ).joinToString(" — ")

                        if (workOrArtist.isNotBlank()) {
                            Text(
                                text = workOrArtist,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (!citation.personnage.isNullOrBlank()) {
                            Text(
                                text = "Personnage : ${citation.personnage}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (citations.size > 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                val available = citations.filter { it.id != citation.id }
                                if (available.isNotEmpty()) {
                                    currentCitation = available.shuffled().first()
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Changer de citation",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = "Avancement par type",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp)
        )

        // 3 rows of 2 cards each, filling available height compactly
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val chunked = stats.chunked(2)
            chunked.forEach { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    pair.forEach { stat ->
                        TypeStatCard(
                            stat = stat,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToScreen(stat.screen) }
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

private fun <T> createStat(
    title: String,
    items: List<T>,
    seenEpList: List<Int?>,
    totalEpList: List<Int?>,
    noteList: List<Float?>,
    etatList: List<String?>,
    icon: ImageVector,
    accentColor: Color,
    screen: AppScreen
): TypeStat {
    val count = items.size
    var seenEp = 0
    var totalEp = 0

    for (i in items.indices) {
        val vu = seenEpList.getOrNull(i)
        val tot = totalEpList.getOrNull(i)
        val etat = etatList.getOrNull(i)

        val itemTot = tot ?: 0
        val isCompleted = etat.equals("Vu", ignoreCase = true) ||
                etat.equals("Terminé", ignoreCase = true) ||
                etat.equals("Lu", ignoreCase = true)
        val itemVu = vu ?: if (isCompleted) (if (itemTot > 0) itemTot else 1) else 0

        seenEp += itemVu
        totalEp += if (itemTot > 0) itemTot else (if (itemVu > 0) itemVu else 1)
    }

    val progress = if (totalEp > 0) (seenEp.toFloat() / totalEp).coerceIn(0f, 1f) else 0f
    val ratedNotes = noteList.asSequence().filterNotNull().filter { it > 0 }.toList()
    val avgNote = if (ratedNotes.isNotEmpty()) ratedNotes.average().toFloat() else null

    return TypeStat(
        title = title,
        count = count,
        seenEp = seenEp,
        totalEp = totalEp,
        progress = progress,
        avgNote = avgNote,
        icon = icon,
        accentColor = accentColor,
        screen = screen
    )
}

@Composable
private fun GlobalStatBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TypeStatCard(
    stat: TypeStat,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row: Icon + Name + Count Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(stat.accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = stat.icon,
                            contentDescription = null,
                            tint = stat.accentColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = stat.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stat.accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = stat.count.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = stat.accentColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            // Middle: Progress bar & percentage
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Avancement",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(stat.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = stat.accentColor
                    )
                }

                LinearProgressIndicator(
                    progress = { stat.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = stat.accentColor,
                    trackColor = stat.accentColor.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round
                )
            }

            // Bottom detail row: Episodes seen & Average Note
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stat.seenEp}/${stat.totalEp} ep/ch",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (stat.avgNote != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = String.format(Locale.FRANCE, "%.1f", stat.avgNote),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
