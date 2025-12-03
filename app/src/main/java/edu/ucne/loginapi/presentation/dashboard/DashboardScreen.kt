@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.ucne.franniel_arias_ap2_p2.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.domain.model.VehicleAlert
import edu.ucne.loginapi.domain.model.VehicleAlertLevel
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToMaintenance: (Int?) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: (String) -> Unit
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
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToChat = onNavigateToChat
    )
}

@Composable
fun DashboardBody(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToMaintenance: (Int?) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: (String) -> Unit
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
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mycar_logo),
                                contentDescription = "MyCarSetting logo",
                                modifier = Modifier.height(120.dp)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No tienes un vehículo agregado",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Añade tu vehículo para ver tu garage y empezar a usar todas las funciones de la app.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            Button(
                                onClick = onNavigateToProfile,
                                modifier = Modifier.fillMaxWidth(0.7f),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text(
                                    text = "Agregar vehículo",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                else -> {
                    DashboardContent(
                        state = state,
                        onNavigateToMaintenance = onNavigateToMaintenance,
                        onNavigateToHistory = onNavigateToHistory,
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToChat = onNavigateToChat
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardUiState,
    onNavigateToMaintenance: (Int?) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val conversationId = remember(state.currentCar?.id) {
        "maintenance_${state.currentCar?.id ?: "default"}"
    }

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
                    onClick = { onNavigateToMaintenance(null) }
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
                text = "Salud del mantenimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            VehicleHealthCard(
                upcoming = state.upcomingTasks.size,
                overdue = state.overdueTasks.size
            )
        }

        item {
            Text(
                text = "Alertas inteligentes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(state.alerts.take(3)) { alert ->
            VehicleAlertCard(
                alert = alert,
                onClick = { onNavigateToMaintenance(alert.relatedTaskId) }
            )
        }

        if (state.alerts.isEmpty()) {
            item {
                Text(
                    text = "No hay alertas por el momento.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Text(
                text = "Asistente de mantenimiento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToChat(conversationId) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Habla con el asistente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Haz preguntas sobre tus tareas próximas, vencidas y el mantenimiento de tu vehículo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNavigateToMaintenance(null) }
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

        item {
            Text(
                text = "Tareas vencidas más urgentes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(state.overdueTasks.take(3)) { task ->
            OverdueTaskCard(
                task = task,
                onClick = { onNavigateToMaintenance(task.id) }
            )
        }

        if (state.overdueTasks.isEmpty()) {
            item {
                Text(
                    text = "No tienes tareas vencidas. ¡Buen trabajo!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VehicleHealthCard(
    upcoming: Int,
    overdue: Int
) {
    val totalTasks = upcoming + overdue
    val rawScore = when {
        totalTasks == 0 -> 100
        overdue == 0 -> 90
        else -> (90 - overdue * 15).coerceIn(20, 90)
    }
    val healthScore = rawScore.coerceIn(0, 100)
    val progress = healthScore / 100f

    val statusText = when {
        healthScore >= 90 -> "Excelente"
        healthScore >= 75 -> "Bueno"
        healthScore >= 55 -> "Regular"
        else -> "Crítico"
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
                text = "Panel de salud del vehículo",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$healthScore%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                    Text(
                        text = "Basado en tareas próximas y vencidas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

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
private fun VehicleAlertCard(
    alert: VehicleAlert,
    onClick: () -> Unit
) {
    val (background, foreground) = when (alert.level) {
        VehicleAlertLevel.CRITICAL -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        VehicleAlertLevel.IMPORTANT -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        VehicleAlertLevel.RECOMMENDATION -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        VehicleAlertLevel.INFO -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = foreground
            )
            Text(
                text = alert.message,
                style = MaterialTheme.typography.bodySmall,
                color = foreground
            )
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

@Composable
private fun OverdueTaskCard(
    task: MaintenanceTask,
    onClick: () -> Unit
) {
    val formatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }
    val dateText = task.dueDateMillis?.let { millis ->
        formatter.format(Date(millis))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            if (task.dueMileageKm != null) {
                Text(
                    text = "A los ${task.dueMileageKm} km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            if (dateText != null) {
                Text(
                    text = "Vencida desde: $dateText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Text(
                text = "Recomendación: atiende esta tarea lo antes posible.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
