package edu.ucne.loginapi.presentation.usuario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUserCar: () -> Unit,
    onLogout: () -> Unit,
    viewModel: UsuarioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userName = state.currentUser?.userName ?: state.userName.ifBlank { "Usuario" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi cuenta",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        ProfileBody(
            padding = padding,
            userName = userName,
            onNavigateToUserCar = onNavigateToUserCar,
            onLogoutClick = {
                viewModel.onEvent(UsuarioEvent.Logout)
                onLogout()
            }
        )
    }
}

@Composable
private fun ProfileBody(
    padding: PaddingValues,
    userName: String,
    onNavigateToUserCar: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Sesión activa",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Mi garaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Configura tus vehículos para recibir recordatorios personalizados de mantenimiento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onNavigateToUserCar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestionar vehículos")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}
