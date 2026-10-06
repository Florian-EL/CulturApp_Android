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
import com.example.culturapp_android.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private var database: AppDatabase =
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "library.db"
        ).build()

    private var repository =
        UserRepository(database.Daos())

    var users by mutableStateOf<List<Film>>(emptyList())
        private set

    var statusMessage by mutableStateOf<String?>(null)
        private set

    private fun setStatus(message: String, isError: Boolean = false) {
        statusMessage = message
        if (isError) {
            Log.e("UserViewModel", message)
        } else {
            Log.i("UserViewModel", message)
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                users = withContext(Dispatchers.IO) {
                    repository.getFilm()
                }
            } catch (e: Exception) {
                users = emptyList()
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
                        }
                    }

                    database = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "library.db"
                    ).fallbackToDestructiveMigration().build()

                    repository = UserRepository(database.Daos())

                    users = repository.getFilm()
                    setStatus(
                        "Base de données '${dbFile.name}' chargée avec succès (${users.size} utilisateurs)",
                        false
                    )
                }
            } catch (e: Exception) {
                setStatus("Erreur lors du chargement : ${e.toString()}", true)
            }
        }
    }
}
