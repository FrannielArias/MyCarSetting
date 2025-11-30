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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToMaintenance: () -> Unit,
    onNavigateToHistory: () -> Unit,
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
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToProfile = onNavigateToProfile
    )
}

@Composable
fun DashboardBody(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToHistory: () -> Unit,
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
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    MyCarLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.currentCar == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                        onNavigateToMaintenance = onNavigateToMaintenance,
                        onNavigateToHistory = onNavigateToHistory,
                        onNavigateToProfile = onNavigateToProfile
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardUiState,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
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

                    val userName = state.userName.ifBlank { "Usuario" }

                    Text(
                        text = userName,
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
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.extraLarge
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                        }
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
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay mantenimientos próximos",
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
                    Text(text = "Mantenimiento")
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToHistory
                ) {
                    Text(text = "Historial mant.")
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
            MaintenanceStatusCard(
                upcoming = state.upcomingTasks.size,
                overdue = state.overdueTasks.size
            )
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
            UpcomingTaskCard(task = task)
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

@Composable
private fun MaintenanceStatusCard(
    upcoming: Int,
    overdue: Int
) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Resumen",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Próximas tareas",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = upcoming.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "Tareas vencidas",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = overdue.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (overdue > 0)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingTaskCard(task: MaintenanceTask) {
    val formatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
    val dateText = task.dueDateMillis?.let { millis ->
        formatter.format(Date(millis))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
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
            if (task.dueMileageKm != null) {
                Text(
                    text = "A los ${task.dueMileageKm} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (dateText != null) {
                Text(
                    text = "Fecha objetivo: $dateText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
