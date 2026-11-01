package com.example.culturapp_android.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.RoomDatabase
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
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()

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
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()

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

    var isImagesLoading by mutableStateOf(false)
        private set

    var imagesVersion by mutableIntStateOf(0)
        private set

    private var copyImagesJob: kotlinx.coroutines.Job? = null

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
                        Log.e("TypeViewModel", "Error loading database from saved folder", e)
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

    suspend fun exportDatabaseToSavedFolder() = withContext(Dispatchers.IO) {
        val context = getApplication<Application>()
        val localDbFile = context.getDatabasePath("library.db")
        if (!localDbFile.exists()) return@withContext

        val prefs = context.getSharedPreferences("culturapp_prefs", Context.MODE_PRIVATE)
        val savedUriStr = prefs.getString("saved_db_folder_uri", null) ?: return@withContext
        val folderUri = Uri.parse(savedUriStr)

        try {
            database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close()
        } catch (e: Exception) {
            Log.w("TypeViewModel", "Checkpoint PRAGMA failed", e)
        }

        val physicalDir = getPhysicalFileFromUri(context, folderUri)
        var physicalDbFile: File? = null
        if (physicalDir != null && physicalDir.exists() && physicalDir.isDirectory) {
            physicalDbFile = physicalDir.listFiles()?.find { file ->
                val name = file.name ?: ""
                name.endsWith(".db", ignoreCase = true) || name.endsWith(
                    ".sqlite",
                    ignoreCase = true
                )
            }
            if (physicalDbFile == null) {
                physicalDbFile = File(physicalDir, "library.db")
            }
        }

        var copied = false
        if (physicalDbFile != null && (physicalDbFile.exists() || physicalDbFile.createNewFile())) {
            if (physicalDbFile.canWrite()) {
                try {
                    FileInputStream(localDbFile).use { input ->
                        FileOutputStream(physicalDbFile).use { output ->
                            input.copyTo(output)
                            output.flush()
                            output.fd.sync()
                        }
                    }
                    copied = true
                    Log.i(
                        "TypeViewModel",
                        "Exported database directly to physical file: ${physicalDbFile.absolutePath}"
                    )
                } catch (e: Exception) {
                    Log.w("TypeViewModel", "Direct physical export failed, falling back to SAF", e)
                }
            }
        }

        if (!copied) {
            val documentDir = DocumentFile.fromTreeUri(context, folderUri) ?: return@withContext
            var targetDoc = documentDir.listFiles()?.find { file ->
                val name = file.name ?: ""
                name.endsWith(".db", ignoreCase = true) || name.endsWith(
                    ".sqlite",
                    ignoreCase = true
                )
            }
            if (targetDoc == null) {
                targetDoc = documentDir.createFile("application/octet-stream", "library.db")
            }

            targetDoc?.uri?.let { uri ->
                context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                    FileInputStream(localDbFile).use { input ->
                        input.copyTo(output)
                        output.flush()
                    }
                    copied = true
                    Log.i("TypeViewModel", "Exported database via SAF to $uri")
                }
            }
        }
    }

    fun saveFilm(film: Film) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.saveFilm(film)
                exportDatabaseToSavedFolder()
                loadFilm()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error saving film", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de l'enregistrement du film : ${e.message}", true)
                }
            }
        }
    }

    fun deleteFilm(film: Film) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.deleteFilm(film)
                exportDatabaseToSavedFolder()
                loadFilm()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error deleting film", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de la suppression : ${e.message}", true)
                }
            }
        }
    }

    fun saveSerie(serie: Serie) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.saveSerie(serie)
                exportDatabaseToSavedFolder()
                loadSerie()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error saving serie", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de l'enregistrement de la série : ${e.message}", true)
                }
            }
        }
    }

    fun deleteSerie(serie: Serie) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.deleteSerie(serie)
                exportDatabaseToSavedFolder()
                loadSerie()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error deleting serie", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de la suppression : ${e.message}", true)
                }
            }
        }
    }

    fun saveRoman(roman: Roman) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.saveRoman(roman)
                exportDatabaseToSavedFolder()
                loadRoman()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error saving roman", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de l'enregistrement du roman : ${e.message}", true)
                }
            }
        }
    }

    fun deleteRoman(roman: Roman) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.deleteRoman(roman)
                exportDatabaseToSavedFolder()
                loadRoman()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error deleting roman", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de la suppression : ${e.message}", true)
                }
            }
        }
    }

    fun saveManga(manga: Manga) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.saveManga(manga)
                exportDatabaseToSavedFolder()
                loadManga()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error saving manga", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de l'enregistrement du manga : ${e.message}", true)
                }
            }
        }
    }

    fun deleteManga(manga: Manga) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.deleteManga(manga)
                exportDatabaseToSavedFolder()
                loadManga()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error deleting manga", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de la suppression : ${e.message}", true)
                }
            }
        }
    }

    fun saveWebtoon(webtoon: Webtoon) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.saveWebtoon(webtoon)
                exportDatabaseToSavedFolder()
                loadWebtoon()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error saving webtoon", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de l'enregistrement du webtoon : ${e.message}", true)
                }
            }
        }
    }

    fun deleteWebtoon(webtoon: Webtoon) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.deleteWebtoon(webtoon)
                exportDatabaseToSavedFolder()
                loadWebtoon()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error deleting webtoon", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de la suppression : ${e.message}", true)
                }
            }
        }
    }

    fun saveWattpad(wattpad: Wattpad) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.saveWattpad(wattpad)
                exportDatabaseToSavedFolder()
                loadWattpad()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error saving wattpad", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de l'enregistrement : ${e.message}", true)
                }
            }
        }
    }

    fun deleteWattpad(wattpad: Wattpad) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                libraryRepository.deleteWattpad(wattpad)
                exportDatabaseToSavedFolder()
                loadWattpad()
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error deleting wattpad", e)
                withContext(Dispatchers.Main) {
                    setStatus("Erreur lors de la suppression : ${e.message}", true)
                }
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

    private fun startBackgroundImagesCopy(context: Context, folderUri: Uri, physicalDir: File?) {
        copyImagesJob?.cancel()
        copyImagesJob = viewModelScope.launch(Dispatchers.IO) {
            val destFile = context.getDatabasePath("library.db")
            val parentDir = destFile.parentFile ?: return@launch
            withContext(Dispatchers.Main) {
                isImagesLoading = true
            }
            try {
                copyImages(context, folderUri, parentDir, physicalDir)
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error copying images in background", e)
            } finally {
                withContext(Dispatchers.Main) {
                    isImagesLoading = false
                    imagesVersion++
                }
            }
        }
    }

    private suspend fun copyImages(
        context: Context,
        folderUri: Uri,
        destDir: File,
        physicalDir: File?
    ) = withContext(Dispatchers.IO) {
        destDir.mkdirs()

        var copiedCount = 0
        var directCopyComplete = false
        if (physicalDir != null && physicalDir.exists() && physicalDir.isDirectory) {
            val imageFiles = physicalDir.listFiles()
                ?.filter { it.isFile && it.name.isSupportedCoverImage() }
                .orEmpty()

            if (imageFiles.isNotEmpty()) {
                var allSucceeded = true
                imageFiles.forEach { file ->
                    val imageDest = File(destDir, file.name)
                    val srcLength = file.length()
                    val srcLastModified = file.lastModified()
                    val shouldCopy = !imageDest.exists() ||
                            imageDest.length() == 0L ||
                            imageDest.length() != srcLength ||
                            (srcLastModified > 0 && srcLastModified > imageDest.lastModified())

                    if (shouldCopy) {
                        try {
                            FileInputStream(file).use { input ->
                                FileOutputStream(imageDest).use { output ->
                                    input.copyTo(output)
                                    output.flush()
                                    output.fd.sync()
                                }
                            }
                            if (srcLastModified > 0) {
                                imageDest.setLastModified(srcLastModified)
                            }
                            copiedCount++
                            if (copiedCount % 10 == 0) {
                                withContext(Dispatchers.Main) {
                                    imagesVersion++
                                }
                            }
                            Log.i(
                                "TypeViewModel",
                                "Copied updated cover ${file.name} to ${imageDest.absolutePath}"
                            )
                        } catch (e: Exception) {
                            allSucceeded = false
                            imageDest.delete()
                            Log.w(
                                "TypeViewModel",
                                "Direct copy failed for cover ${file.absolutePath}; will try SAF",
                                e
                            )
                        }
                    }
                }
                if (allSucceeded) {
                    directCopyComplete = true
                }
            }
        }

        // SAF fallback when direct physical access is not available or incomplete
        if (!directCopyComplete) {
            val documentDir = DocumentFile.fromTreeUri(context, folderUri)
            val imageFiles = documentDir?.listFiles()
                ?.filter { it.isFile && it.name?.isSupportedCoverImage() == true }
                .orEmpty()

            imageFiles.forEach { file ->
                val name = file.name ?: return@forEach
                val imageDest = File(destDir, name)
                val srcLength = file.length()
                val srcLastModified = file.lastModified()
                val shouldCopy = !imageDest.exists() ||
                        imageDest.length() == 0L ||
                        imageDest.length() != srcLength ||
                        (srcLastModified > 0 && srcLastModified > imageDest.lastModified())

                if (shouldCopy) {
                    try {
                        scanAndCopyFile(context, file.uri, imageDest)
                        if (srcLastModified > 0) {
                            imageDest.setLastModified(srcLastModified)
                        }
                        copiedCount++
                        if (copiedCount % 10 == 0) {
                            withContext(Dispatchers.Main) {
                                imagesVersion++
                            }
                        }
                        Log.i(
                            "TypeViewModel",
                            "Copied updated SAF cover $name to ${imageDest.absolutePath}"
                        )
                    } catch (e: Exception) {
                        imageDest.delete()
                        Log.w("TypeViewModel", "Failed to copy SAF image $name", e)
                    }
                }
            }
            if (documentDir == null) {
                Log.w("TypeViewModel", "Could not open selected folder through SAF: $folderUri")
            }
        }
    }

    private fun String.isSupportedCoverImage(): Boolean =
        endsWith(".jpg", ignoreCase = true) ||
                endsWith(".jpeg", ignoreCase = true) ||
                endsWith(".png", ignoreCase = true) ||
                endsWith(".webp", ignoreCase = true)

    private suspend fun loadDatabaseFromFolderInternal(folderUri: Uri) =
        withContext(Dispatchers.IO) {
            val context = getApplication<Application>()

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

            // Copy missing images asynchronously in background without blocking initial DB load
            destFile.parentFile?.let {
                startBackgroundImagesCopy(context, folderUri, physicalDir)
            }
        }
}
