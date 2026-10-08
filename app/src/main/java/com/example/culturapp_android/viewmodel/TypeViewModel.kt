package com.example.culturapp_android.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.culturapp_android.data.AppDatabase
import com.example.culturapp_android.data.Film
import com.example.culturapp_android.data.LibraryRepository
import com.example.culturapp_android.data.Manga
import com.example.culturapp_android.data.Roman
import com.example.culturapp_android.data.Serie
import com.example.culturapp_android.data.Wattpad
import com.example.culturapp_android.data.Webtoon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TypeViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var database: AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "library.db"
        ).build()

    private var libraryRepository =
        LibraryRepository(
            database.filmdaos(),
            database.seriedaos(),
            database.romandaos(),
            database.mangadaos(),
            database.webtoondaos(),
            database.wattpaddaos(),
        )

    var films by mutableStateOf<List<Film>>(emptyList())
        private set

    var series by mutableStateOf<List<Serie>>(emptyList())
        private set

    var romans by mutableStateOf<List<Roman>>(emptyList())
        private set

    var mangas by mutableStateOf<List<Manga>>(emptyList())
        private set

    var webtoon by mutableStateOf<List<Webtoon>>(emptyList())
        private set

    var wattpad by mutableStateOf<List<Wattpad>>(emptyList())
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadFilm()
        loadSerie()
        loadRoman()
        loadManga()
    }

    private fun setStatus(message: String, isError: Boolean = false) {
        statusMessage = message
        if (isError) {
            Log.e("TypeViewModel", message)
        } else {
            Log.i("TypeViewModel", message)
        }
    }

    fun loadFilm() {
        viewModelScope.launch {
            try {
                films = withContext(Dispatchers.IO) {
                    libraryRepository.getFilm()
                }
            } catch (e: Exception) {
                films = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadSerie() {
        viewModelScope.launch {
            try {
                series = withContext(Dispatchers.IO) {
                    libraryRepository.getSerie()
                }
            } catch (e: Exception) {
                series = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadRoman() {
        viewModelScope.launch {
            try {
                romans = withContext(Dispatchers.IO) {
                    libraryRepository.getRoman()
                }
            } catch (e: Exception) {
                romans = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadManga() {
        viewModelScope.launch {
            try {
                mangas = withContext(Dispatchers.IO) {
                    libraryRepository.getManga()
                }
            } catch (e: Exception) {
                mangas = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadWebtoon() {
        viewModelScope.launch {
            try {
                webtoon = withContext(Dispatchers.IO) {
                    libraryRepository.getWebtoon()
                }
            } catch (e: Exception) {
                webtoon = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadWattpad() {
        viewModelScope.launch {
            try {
                wattpad = withContext(Dispatchers.IO) {
                    libraryRepository.getWattpad()
                }
            } catch (e: Exception) {
                wattpad = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }
    fun loadDatabaseFromFolder(folderUri: Uri) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val context = getApplication<Application>()
                    val documentDir = DocumentFile.fromTreeUri(context, folderUri)
                    if ((documentDir == null) || !documentDir.isDirectory) {
                        setStatus("Dossier sélectionné invalide", true)
                        return@withContext
                    }

                    val files = documentDir.listFiles()
                    if (files == null) {
                        setStatus("Erreur : Impossible de lister les fichiers du dossier", true)
                        return@withContext
                    }

                    val dbFile = files.find { file ->
                        val name = file.name ?: ""
                        name.endsWith(".db", ignoreCase = true) || name.endsWith(
                            ".sqlite",
                            ignoreCase = true
                        )
                    }

                    if (dbFile == null) {
                        setStatus(
                            "Aucun fichier de base de données (.db ou .sqlite) trouvé dans le dossier",
                            true
                        )
                        return@withContext
                    }

                    try {
                        database.close()
                    } catch (_: Exception) {
                    }

                    val destFile = context.getDatabasePath("library.db")
                    destFile.parentFile?.mkdirs()

                    context.contentResolver.openInputStream(dbFile.uri)?.use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                        ?: throw IllegalStateException("Impossible d'ouvrir le flux pour ${dbFile.name}")

                    val dbFileName = dbFile.name ?: "library.db"
                    files.forEach { file ->
                        val name = file.name ?: ""
                        if (name.startsWith(dbFileName) && (name.endsWith("-wal") || name.endsWith("-shm"))) {
                            val suffix = name.removePrefix(dbFileName)
                            val walDest = context.getDatabasePath("library.db$suffix")
                            context.contentResolver.openInputStream(file.uri)?.use { input ->
                                walDest.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                        } else if (name.endsWith(".jpg", ignoreCase = true) ||
                            name.endsWith(".jpeg", ignoreCase = true) ||
                            name.endsWith(".png", ignoreCase = true) ||
                            name.endsWith(".webp", ignoreCase = true)
                        ) {
                            val imageDest = File(destFile.parentFile, name)
                            context.contentResolver.openInputStream(file.uri)?.use { input ->
                                imageDest.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                        }
                    }

                    database = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "library.db"
                    ).fallbackToDestructiveMigration().build()

                    libraryRepository = LibraryRepository(
                        database.filmdaos(),
                        database.seriedaos(),
                        database.romandaos(),
                        database.mangadaos(),
                        database.webtoondaos(),
                        database.wattpaddaos(),
                    )

                    films = libraryRepository.getFilm()
                    series = libraryRepository.getSerie()
                    romans = libraryRepository.getRoman()
                    mangas = libraryRepository.getManga()
                    webtoon = libraryRepository.getWebtoon()
                    wattpad = libraryRepository.getWattpad()
                    setStatus(
                        "",
                        false
                    )
                }
            } catch (e: Exception) {
                setStatus("Erreur lors du chargement : ${e.toString()}", true)
            }
        }
    }
}
