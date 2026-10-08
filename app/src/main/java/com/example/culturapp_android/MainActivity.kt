package com.example.culturapp_android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.culturapp_android.ui.theme.FilmScreen
import com.example.culturapp_android.ui.theme.MangaScreen
import com.example.culturapp_android.ui.theme.RomanScreen
import com.example.culturapp_android.ui.theme.SerieScreen
import com.example.culturapp_android.ui.theme.WattpadScreen
import com.example.culturapp_android.ui.theme.WebtoonScreen
import com.example.culturapp_android.viewmodel.TypeViewModel
import kotlinx.coroutines.launch

enum class AppScreen {
    FILMS, SERIES, ROMAN, MANGA, WEBTOON, WATTPAD
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: TypeViewModel = viewModel()
            var currentScreen by remember { mutableStateOf(AppScreen.FILMS) }
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { uri ->
                uri?.let {
                    try {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {
                    }
                    viewModel.loadDatabaseFromFolder(it)
                }
            }

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "CulturApp",
                            modifier = Modifier.padding(8.dp)
                        )
                        Button(
                            onClick = { launcher.launch(null) },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text("Sélectionner un dossier de base de données")
                        }

                        if (!viewModel.statusMessage.isNullOrEmpty()) {
                            Spacer(
                                modifier = Modifier
                                    .height(8.dp)
                                    .weight(1f)
                            )
                            Text(
                                text = viewModel.statusMessage.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        NavigationDrawerItem(
                            label = { Text("Films") },
                            selected = currentScreen == AppScreen.FILMS,
                            onClick = {
                                currentScreen = AppScreen.FILMS
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        NavigationDrawerItem(
                            label = { Text("Séries") },
                            selected = currentScreen == AppScreen.SERIES,
                            onClick = {
                                currentScreen = AppScreen.SERIES
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        NavigationDrawerItem(
                            label = { Text("Roman") },
                            selected = currentScreen == AppScreen.ROMAN,
                            onClick = {
                                currentScreen = AppScreen.ROMAN
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        NavigationDrawerItem(
                            label = { Text("Manga") },
                            selected = currentScreen == AppScreen.MANGA,
                            onClick = {
                                currentScreen = AppScreen.MANGA
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        NavigationDrawerItem(
                            label = { Text("Webtoon") },
                            selected = currentScreen == AppScreen.WEBTOON,
                            onClick = {
                                currentScreen = AppScreen.WEBTOON
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        NavigationDrawerItem(
                            label = { Text("Wattpad") },
                            selected = currentScreen == AppScreen.WATTPAD,
                            onClick = {
                                currentScreen = AppScreen.WATTPAD
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                    }
                }
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("CulturApp") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()) {
                        when (currentScreen) {
                            AppScreen.FILMS -> {
                                FilmScreen(
                                    film = viewModel.films,
                                )
                            }

                            AppScreen.SERIES -> {
                                SerieScreen(
                                    serie = viewModel.series,
                                )
                            }

                            AppScreen.ROMAN -> {
                                RomanScreen(
                                    roman = viewModel.romans,
                                )
                            }

                            AppScreen.MANGA -> {
                                MangaScreen(
                                    manga = viewModel.mangas,
                                )
                            }

                            AppScreen.WEBTOON -> {
                                WebtoonScreen(
                                    webtoon = viewModel.webtoon,
                                )
                            }

                            AppScreen.WATTPAD -> {
                                WattpadScreen(
                                    wattpad = viewModel.wattpad,
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.loadFilm()
                viewModel.loadSerie()
                viewModel.loadRoman()
                viewModel.loadManga()
                viewModel.loadWebtoon()
                viewModel.loadWattpad()
            }
        }
    }
}
