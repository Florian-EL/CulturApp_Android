package com.example.culturapp_android

import android.content.Intent
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.culturapp_android.ui.theme.UserScreen
import com.example.culturapp_android.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?,
    ) {

        super.onCreate(savedInstanceState)

        setContent {

            val viewModel: UserViewModel = viewModel()

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

            UserScreen(
                users = viewModel.users,
                statusMessage = viewModel.statusMessage
            ) {
                launcher.launch(null)
            }

            LaunchedEffect(Unit) {
                viewModel.loadUsers()
            }
        }
    }
}
