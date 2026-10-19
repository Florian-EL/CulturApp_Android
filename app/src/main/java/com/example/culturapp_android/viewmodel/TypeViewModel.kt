package com.example.culturapp_android.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
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
import java.io.FileInputStream
import java.io.FileOutputStream

class TypeViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var database: AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "library.db"
        ).fallbackToDestructiveMigration().build()

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
        try {
            database.close()
        } catch (e: Exception) {
            Log.w("TypeViewModel", "Error closing old database", e)
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

    var isLoading by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                val context = getApplication<Application>()
                val prefs = context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
                val savedUriStr = prefs.getString("saved_db_folder_uri", null)
                if (!savedUriStr.isNullOrEmpty()) {
                    try {
                        loadDatabaseFromFolderInternal(Uri.parse(savedUriStr))
                    } catch (e: Exception) {
                        Log.e("TypeViewModel", "Error refreshing from saved folder", e)
                        withContext(Dispatchers.Main) {
                            setStatus("Erreur lors de la synchronisation : ${e.message}", true)
                        }
                        loadAllData()
                    }
                } else {
                    loadAllData()
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun showError(message: String) {
        setStatus(message, true)
    }

    fun loadAll() {
        viewModelScope.launch {
            loadAllData()
        }
    }

    private suspend fun loadAllData() {
        isLoading = true
        try {
            val f = withContext(Dispatchers.IO) { libraryRepository.getFilm() }
            val s = withContext(Dispatchers.IO) { libraryRepository.getSerie() }
            val r = withContext(Dispatchers.IO) { libraryRepository.getRoman() }
            val m = withContext(Dispatchers.IO) { libraryRepository.getManga() }
            val w = withContext(Dispatchers.IO) { libraryRepository.getWebtoon() }
            val wt = withContext(Dispatchers.IO) { libraryRepository.getWattpad() }

            withContext(Dispatchers.Main) {
                films = f
                series = s
                romans = r
                mangas = m
                webtoon = w
                wattpad = wt
            }
            Log.i(
                "TypeViewModel",
                "Data loaded: Films=${f.size}, Series=${s.size}, Romans=${r.size}, Mangas=${m.size}, Webtoon=${w.size}, Wattpad=${wt.size}"
            )
        } catch (e: Exception) {
            Log.e("TypeViewModel", "Error loading data from repository", e)
            withContext(Dispatchers.Main) {
                setStatus("Erreur de lecture de la base : ${e.message}", true)
            }
        } finally {
            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
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
                films = withContext(Dispatchers.IO) { libraryRepository.getFilm() }
            } catch (e: Exception) {
                films = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadSerie() {
        viewModelScope.launch {
            try {
                series = withContext(Dispatchers.IO) { libraryRepository.getSerie() }
            } catch (e: Exception) {
                series = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadRoman() {
        viewModelScope.launch {
            try {
                romans = withContext(Dispatchers.IO) { libraryRepository.getRoman() }
            } catch (e: Exception) {
                romans = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadManga() {
        viewModelScope.launch {
            try {
                mangas = withContext(Dispatchers.IO) { libraryRepository.getManga() }
            } catch (e: Exception) {
                mangas = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadWebtoon() {
        viewModelScope.launch {
            try {
                webtoon = withContext(Dispatchers.IO) { libraryRepository.getWebtoon() }
            } catch (e: Exception) {
                webtoon = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    fun loadWattpad() {
        viewModelScope.launch {
            try {
                wattpad = withContext(Dispatchers.IO) { libraryRepository.getWattpad() }
            } catch (e: Exception) {
                wattpad = emptyList()
                setStatus(e.toString(), true)
            }
        }
    }

    private fun getPhysicalFileFromUri(context: Context, uri: Uri): File? {
        try {
            if (uri.authority == "com.android.externalstorage.documents") {
                val docId = if (DocumentsContract.isDocumentUri(context, uri)) {
                    DocumentsContract.getDocumentId(uri)
                } else {
                    DocumentsContract.getTreeDocumentId(uri)
                }
                if (!docId.isNullOrEmpty() && docId.contains(":")) {
                    val split = docId.split(":")
                    val type = split[0]
                    val relativePath = if (split.size > 1) split[1] else ""

                    val rootPath = if (type.equals("primary", ignoreCase = true)) {
                        Environment.getExternalStorageDirectory().absolutePath
                    } else {
                        "/storage/$type"
                    }

                    val candidate = File(rootPath, relativePath)
                    if (candidate.exists()) {
                        return candidate
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("TypeViewModel", "Could not resolve physical file path from $uri", e)
        }
        return null
    }

    private fun copyUriToFile(context: Context, uri: Uri, destFile: File) {
        var copied = false
        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                FileInputStream(pfd.fileDescriptor).use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                        output.flush()
                        output.fd.sync()
                    }
                }
                copied = true
            }
        } catch (e: Exception) {
            Log.w(
                "TypeViewModel",
                "openFileDescriptor failed for $uri, falling back to openInputStream",
                e
            )
        }

        if (!copied) {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                    output.fd.sync()
                }
            } ?: throw IllegalStateException("Impossible d'ouvrir le flux pour $uri")
        }
    }

    private suspend fun scanAndCopyFile(context: Context, uri: Uri, destFile: File) =
        withContext(Dispatchers.IO) {
            val physicalFile = getPhysicalFileFromUri(context, uri)
            var copied = false

            if (physicalFile != null && physicalFile.exists()) {
                try {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(physicalFile.absolutePath),
                        null,
                        null
                    )
                } catch (e: Exception) {
                    Log.w(
                        "TypeViewModel",
                        "MediaScanner failed for ${physicalFile.absolutePath}",
                        e
                    )
                }

                try {
                    if (physicalFile.canRead()) {
                        FileInputStream(physicalFile).use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                                output.flush()
                                output.fd.sync()
                            }
                        }
                        copied = true
                        Log.i(
                            "TypeViewModel",
                            "Direct physical copy successful: ${physicalFile.absolutePath} -> ${destFile.absolutePath} (${destFile.length()} bytes)"
                        )
                    }
                } catch (e: Exception) {
                    Log.w("TypeViewModel", "Direct physical copy failed, falling back to SAF", e)
                }
            }

            if (!copied) {
                copyUriToFile(context, uri, destFile)
                Log.i(
                    "TypeViewModel",
                    "SAF copy successful: $uri -> ${destFile.absolutePath} (${destFile.length()} bytes)"
                )
            }
        }

    fun loadDatabaseFromFolder(folderUri: Uri) {
        viewModelScope.launch {
            isLoading = true
            try {
                loadDatabaseFromFolderInternal(folderUri)
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Failed to load database from folder", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors du chargement du dossier : ${e.message}", true)
                }
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun clearDatabaseCache(context: Context) {
        withContext(Dispatchers.Main) {
            try {
                database.close()
            } catch (e: Exception) {
                Log.w("TypeViewModel", "Error closing database", e)
            }
            films = emptyList()
            series = emptyList()
            romans = emptyList()
            mangas = emptyList()
            webtoon = emptyList()
            wattpad = emptyList()
        }

        withContext(Dispatchers.IO) {
            val destFile = context.getDatabasePath("library.db")
            if (destFile.parentFile?.exists() == true) {
                destFile.parentFile?.listFiles()?.forEach { file ->
                    val name = file.name
                    if (name.startsWith("library.db") &&
                        !name.endsWith(".jpg", ignoreCase = true) &&
                        !name.endsWith(".png", ignoreCase = true) &&
                        !name.endsWith(".jpeg", ignoreCase = true) &&
                        !name.endsWith(".webp", ignoreCase = true)
                    ) {
                        try {
                            file.delete()
                        } catch (e: Exception) {
                            Log.w("TypeViewModel", "Could not delete database file $name", e)
                        }
                    }
                }
            }
            val stagedDb = File(context.cacheDir, "library-import.db")
            stagedDb.delete()
        }
    }

    private fun copyImagesInBackground(
        context: Context,
        folderUri: Uri,
        destDir: File,
        physicalDir: File?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (physicalDir != null && physicalDir.exists() && physicalDir.isDirectory) {
                    physicalDir.listFiles()?.forEach { file ->
                        val name = file.name ?: ""
                        if (name.endsWith(".jpg", ignoreCase = true) ||
                            name.endsWith(".jpeg", ignoreCase = true) ||
                            name.endsWith(".png", ignoreCase = true) ||
                            name.endsWith(".webp", ignoreCase = true)
                        ) {
                            val imageDest = File(destDir, name)
                            if (!imageDest.exists() || imageDest.length() == 0L) {
                                try {
                                    FileInputStream(file).use { input ->
                                        FileOutputStream(imageDest).use { output ->
                                            input.copyTo(output)
                                            output.flush()
                                            output.fd.sync()
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.w("TypeViewModel", "Failed to copy physical image $name", e)
                                }
                            }
                        }
                    }
                } else {
                    val documentDir = DocumentFile.fromTreeUri(context, folderUri)
                    documentDir?.listFiles()?.forEach { file ->
                        val name = file.name ?: ""
                        if (name.endsWith(".jpg", ignoreCase = true) ||
                            name.endsWith(".jpeg", ignoreCase = true) ||
                            name.endsWith(".png", ignoreCase = true) ||
                            name.endsWith(".webp", ignoreCase = true)
                        ) {
                            val imageDest = File(destDir, name)
                            if (!imageDest.exists() || imageDest.length() == 0L) {
                                try {
                                    scanAndCopyFile(context, file.uri, imageDest)
                                } catch (e: Exception) {
                                    Log.w("TypeViewModel", "Failed to copy SAF image $name", e)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("TypeViewModel", "Error copying images in background", e)
            }
        }
    }

    private suspend fun loadDatabaseFromFolderInternal(folderUri: Uri) =
        withContext(Dispatchers.IO) {
            val context = getApplication<Application>()
            Log.i("TypeViewModel", "Loading database from folderUri: $folderUri")

            // First, purge and clear all database cache completely before loading
            clearDatabaseCache(context)

            val destFile = context.getDatabasePath("library.db")
            destFile.parentFile?.mkdirs()

            val stagedDb = File(context.cacheDir, "library-import.db")

            // 1. Try direct physical file listing first if folder resolves to a physical directory (bypasses SAF caching)
            val physicalDir = getPhysicalFileFromUri(context, folderUri)
            val physicalDbFile =
                if (physicalDir != null && physicalDir.exists() && physicalDir.isDirectory) {
                    physicalDir.listFiles()?.find { file ->
                        val name = file.name ?: ""
                        name.endsWith(".db", ignoreCase = true) || name.endsWith(
                            ".sqlite",
                            ignoreCase = true
                        )
                    }
                } else {
                    null
                }

            if (physicalDbFile != null && physicalDbFile.exists() && physicalDbFile.canRead()) {
                Log.i(
                    "TypeViewModel",
                    "Found physical database file directly: ${physicalDbFile.absolutePath}"
                )
                FileInputStream(physicalDbFile).use { input ->
                    FileOutputStream(stagedDb).use { output ->
                        input.copyTo(output)
                        output.flush()
                        output.fd.sync()
                    }
                }
            } else {
                val documentDir = DocumentFile.fromTreeUri(context, folderUri)
                if (documentDir == null || !documentDir.isDirectory) {
                    val prefs =
                        context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
                    prefs.edit().remove("saved_db_folder_uri").apply()
                    withContext(Dispatchers.Main) {
                        setStatus(
                            "Permission ou accès au dossier perdu. Veuillez le re-sélectionner.",
                            true
                        )
                    }
                    return@withContext
                }

                val files = documentDir.listFiles()
                if (files == null) {
                    withContext(Dispatchers.Main) {
                        setStatus("Erreur : Impossible de lister les fichiers du dossier", true)
                    }
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
                    withContext(Dispatchers.Main) {
                        setStatus(
                            "Aucun fichier de base de données (.db ou .sqlite) trouvé dans le dossier",
                            true
                        )
                    }
                    return@withContext
                }

                Log.i(
                    "TypeViewModel",
                    "Found database file via SAF: ${dbFile.name}, uri: ${dbFile.uri}"
                )
                scanAndCopyFile(context, dbFile.uri, stagedDb)
            }

            if (stagedDb.length() == 0L) {
                throw IllegalStateException("Le fichier de base de données importé est vide")
            }

            stagedDb.copyTo(destFile, overwrite = true)

            FileOutputStream(destFile, true).use { fos ->
                fos.flush()
                fos.fd.sync()
            }

            withContext(Dispatchers.Main) {
                reopenLocalDatabase(context)
            }

            stagedDb.delete()

            val prefs = context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("saved_db_folder_uri", folderUri.toString()).apply()

            // Instantly load the new database data into UI!
            loadAllData()

            withContext(Dispatchers.Main) {
                val totalItems =
                    films.size + series.size + romans.size + mangas.size + webtoon.size + wattpad.size
                // setStatus("Données actualisées ($totalItems éléments : ${films.size} films, ${series.size} séries, ${romans.size} romans, ${mangas.size} mangas, ${webtoon.size} webtoons, ${wattpad.size} wattpad).", false)
            }

            // Copy missing images asynchronously in background without blocking UI
            destFile.parentFile?.let { parentDir ->
                copyImagesInBackground(context, folderUri, parentDir, physicalDir)
        }
    }
}
