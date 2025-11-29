@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToMaintenance: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(DashboardEvent.Refresh)
    }

    DashboardBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToMaintenance = onNavigateToMaintenance,
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun DashboardBody(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val snackState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackState.showSnackbar(it)
            onEvent(DashboardEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackState) }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.currentCar == null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sin vehículo configurado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Configura un vehículo para ver tu garage, recordatorios y recomendaciones.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onNavigateToProfile) {
                            Text(text = "Configurar vehículo")
                        }
                    }
                }
            }

            else -> {
                DashboardContent(
                    state = state,
                    modifier = Modifier.padding(padding),
                    onNavigateToMaintenance = onNavigateToMaintenance,
                    onNavigateToProfile = onNavigateToProfile
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardUiState,
    modifier: Modifier = Modifier,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Hola",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = state.currentCar?.brand ?: "Bienvenido a MyCarSetting",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onNavigateToProfile) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Perfil"
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Vehículo principal",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${state.currentCar?.brand} ${state.currentCar?.model}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    state.currentCar?.year?.let { year ->
                        Text(
                            text = "$year • ${state.currentCar.usageType}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    val nextTask = state.upcomingTasks.firstOrNull()
                    if (nextTask != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Próximo mantenimiento",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = nextTask.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMaintenance
                ) {
                    Text(text = "Historial Mant.")
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = {}
                ) {
                    Text(text = "Testigos Tablero")
                }
            }
        }

        item {
            Text(
                text = "Estado del mantenimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Próximas tareas: ${state.upcomingTasks.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Tareas vencidas: ${state.overdueTasks.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToMaintenance
            ) {
                Text(
                    text = "Gestionar mantenimiento",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item {
            Text(
                text = "Siguientes tareas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(state.upcomingTasks.take(3)) { task ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    task.dueMileageKm?.let {
                        Text(
                            text = "A los $it km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (state.upcomingTasks.isEmpty()) {
            item {
                Text(
                    text = "No hay tareas próximas. ¡Tu vehículo está al día!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}