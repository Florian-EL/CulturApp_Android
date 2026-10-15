package com.example.culturapp_android.viewmodel

import android.app.Application
import android.content.Context
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

    private fun reopenLocalDatabase(context: Context) {
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
    }

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
        refreshSavedFolderOnStartup()
    }

    private fun refreshSavedFolderOnStartup() {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
        val savedUriStr = prefs.getString("saved_db_folder_uri", null)
        if (!savedUriStr.isNullOrEmpty()) {
            try {
                loadDatabaseFromFolder(Uri.parse(savedUriStr))
            } catch (e: Exception) {
                loadAll()
            }
        } else {
            loadAll()
        }
    }

    fun showError(message: String) {
        setStatus(message, true)
    }

    fun loadAll() {
        loadFilm()
        loadSerie()
        loadRoman()
        loadManga()
        loadWebtoon()
        loadWattpad()
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
                    val list = libraryRepository.getFilm()
                    Log.i("TypeViewModel", "Loaded films count: ${list.size}")
                    list
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
            var databaseClosed = false
            try {
                var errorMessage: String? = null
                withContext(Dispatchers.IO) {
                    val context = getApplication<Application>()
                    val documentDir = DocumentFile.fromTreeUri(context, folderUri)
                    if ((documentDir == null) || !documentDir.isDirectory) {
                        val prefs =
                            context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
                        prefs.edit().remove("saved_db_folder_uri").apply()
                        errorMessage =
                            "Permission ou accès au dossier perdu. Veuillez le re-sélectionner."
                        return@withContext
                    }

                    val files = documentDir.listFiles()
                    if (files == null) {
                        errorMessage = "Erreur : Impossible de lister les fichiers du dossier"
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
                        errorMessage =
                            "Aucun fichier de base de données (.db ou .sqlite) trouvé dans le dossier"
                        return@withContext
                    }

                    val destFile = context.getDatabasePath("library.db")
                    destFile.parentFile?.mkdirs()
                    val dbFileName = dbFile.name ?: "library.db"
                    val sourceWal = files.firstOrNull { it.name == "$dbFileName-wal" }
                    val stagedDb = File(context.cacheDir, "library-import.db")
                    val stagedWal = File(context.cacheDir, "library-import.db-wal")
                    stagedDb.delete()
                    stagedWal.delete()

                    context.contentResolver.openInputStream(dbFile.uri)?.use { input ->
                        stagedDb.outputStream().use { output -> input.copyTo(output) }
                    } ?: throw IllegalStateException("Impossible d'ouvrir le flux pour $dbFileName")
                    if (stagedDb.length() == 0L) {
                        throw IllegalStateException("Le fichier $dbFileName est vide")
                    }
                    if (sourceWal != null) {
                        context.contentResolver.openInputStream(sourceWal.uri)?.use { input ->
                            stagedWal.outputStream().use { output -> input.copyTo(output) }
                        }
                            ?: throw IllegalStateException("Impossible de lire le journal de $dbFileName")
                    }

                    // Un fichier SHM est un index temporaire propre à la connexion SQLite ;
                    // il doit être recréé par SQLite, jamais importé depuis le dossier source.
                    database.close()
                    databaseClosed = true
                    val localWal = File(destFile.parentFile, "library.db-wal")
                    val localShm = File(destFile.parentFile, "library.db-shm")
                    val localJournal = File(destFile.parentFile, "library.db-journal")
                    localWal.delete()
                    localShm.delete()
                    localJournal.delete()
                    stagedDb.copyTo(destFile, overwrite = true)
                    if (sourceWal != null) stagedWal.copyTo(localWal, overwrite = true)

                    files.forEach { file ->
                        val name = file.name ?: ""
                        if (name.endsWith(".jpg", ignoreCase = true) ||
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
                    reopenLocalDatabase(context)
                    databaseClosed = false
                    stagedDb.delete()
                    stagedWal.delete()
                }

                if (errorMessage != null) {
                    setStatus(errorMessage, true)
                } else {
                    val context = getApplication<Application>()
                    val prefs =
                        context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("saved_db_folder_uri", folderUri.toString()).apply()

                    setStatus("Données actualisées depuis le dossier sélectionné.", false)
                    loadAll()
                }
            } catch (e: Exception) {
                if (databaseClosed) {
                    try {
                        reopenLocalDatabase(getApplication())
                    } catch (reopenError: Exception) {
                        Log.e("TypeViewModel", "Unable to reopen local database", reopenError)
                    }
                }
                if (e is SecurityException) {
                    val context = getApplication<Application>()
                    context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
                        .edit().remove("saved_db_folder_uri").apply()
                    setStatus(
                        "L'accès Android au dossier a expiré. Sélectionnez-le à nouveau pour le mémoriser.",
                        true
                    )
                    return@launch
                }
                setStatus("Erreur lors du chargement : ${e.toString()}", true)
            }
        }
    }

    fun reloadDatabase() {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
        val savedUriStr = prefs.getString("saved_db_folder_uri", null)
        if (!savedUriStr.isNullOrEmpty()) {
            try {
                loadDatabaseFromFolder(Uri.parse(savedUriStr))
            } catch (e: Exception) {
                setStatus("Erreur lors du rafraîchissement : ${e.message}", true)
            }
        } else {
            setStatus("Aucun dossier de base de données enregistré", true)
            loadAll()
        }
    }
}
