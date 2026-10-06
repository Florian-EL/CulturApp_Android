package com.example.culturapp_android.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.culturapp_android.data.Film

@Composable
fun UserScreen(
    users: List<Film>,
    statusMessage: String?,
    onSelectFolder: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "CulturApp",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onSelectFolder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sélectionner un dossier de base de données")
        }

        if (!statusMessage.isNullOrEmpty()) {
            Spacer(
                modifier = Modifier
                    .height(8.dp)
                    .weight(1f)
            )
            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        UserHeader()

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            items(users) { user ->

                UserRow(film = user)

                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun UserHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = "Titre",
            modifier = Modifier.weight(5f)
        )

        Text(
            text = "Note",
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = "Nb_vu",
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = "Année vu",
            modifier = Modifier.weight(1.5f)
        )
    }
}


@Composable
private fun UserRow(
    film: Film,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Text(
            text = film.titre ?: "",
            modifier = Modifier.weight(5f)
        )

        Text(
            text = film.note?.toString() ?: "",
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = film.nb_vu?.toString() ?: "",
            modifier = Modifier.weight(1.5f)
        )

        Text(
            text = film.annee_vu?.toString() ?: "",
            modifier = Modifier.weight(1.5f)
        )
    }
}
