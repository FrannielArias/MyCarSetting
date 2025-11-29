@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.maintenance

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.MaintenanceTask
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceScreen(
    viewModel: MaintenanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(MaintenanceEvent.Refresh)
    }

    MaintenanceBody(
        state = state,
        onEvent = viewModel::onEvent,
        onCreateTask = { viewModel.createTask() }
    )
}

@Composable
fun MaintenanceBody(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit,
    onCreateTask: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.userMessage) {
        val message = state.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onEvent(MaintenanceEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.currentCar?.let { "${it.brand} ${it.model}" }
                            ?: "Mantenimiento",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(MaintenanceEvent.ShowCreateSheet) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar recordatorio")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Cargando recordatorios...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                state.currentCar == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Configura un vehículo para ver recordatorios",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    MaintenanceContent(
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }

        if (state.showCreateSheet) {
            ModalBottomSheet(
                onDismissRequest = { onEvent(MaintenanceEvent.HideCreateSheet) },
                sheetState = sheetState
            ) {
                MaintenanceCreateSheet(
                    state = state,
                    onEvent = onEvent,
                    onCreateTask = onCreateTask
                )
            }
        }
    }
}

@Composable
fun MaintenanceContent(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.overdueTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Vencidas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(state.overdueTasks, key = { "overdue-${it.id}" }) { task ->
                MaintenanceTaskItem(
                    task = task,
                    isOverdue = true,
                    onComplete = { onEvent(MaintenanceEvent.OnCompleteTask(task.id)) },
                    onDelete = { onEvent(MaintenanceEvent.OnDeleteTask(task.id)) }
                )
            }
        }

        if (state.upcomingTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Próximas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(state.upcomingTasks, key = { "upcoming-${it.id}" }) { task ->
                MaintenanceTaskItem(
                    task = task,
                    isOverdue = false,
                    onComplete = { onEvent(MaintenanceEvent.OnCompleteTask(task.id)) },
                    onDelete = { onEvent(MaintenanceEvent.OnDeleteTask(task.id)) }
                )
            }
        }

        if (state.overdueTasks.isEmpty() && state.upcomingTasks.isEmpty() && !state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No hay recordatorios de mantenimiento",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Pulsa el botón + para agregar el primero.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MaintenanceTaskItem(
    task: MaintenanceTask,
    isOverdue: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TaskDetails(
                    task = task,
                    isOverdue = isOverdue,
                    modifier = Modifier.weight(1f)
                )
                TaskActions(
                    onComplete = onComplete,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun TaskDetails(
    task: MaintenanceTask,
    isOverdue: Boolean,
    modifier: Modifier = Modifier
) {
    val dateText = task.dueDateMillis?.let {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.format(Date(it))
    }

    Column(modifier = modifier) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (!task.description.isNullOrBlank()) {
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.type.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (dateText != null) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (task.dueMileageKm != null) {
            val textColor = if (isOverdue) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }

            Text(
                text = "Próximo a los ${task.dueMileageKm} km",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

@Composable
private fun TaskActions(
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onComplete) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Marcar completada"
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar"
            )
        }
    }
}

@Composable
fun MaintenanceCreateSheet(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit,
    onCreateTask: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Nuevo recordatorio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            state.currentCar?.let { car ->
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "Configura un recordatorio rápido para que no olvides tu próximo mantenimiento.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = state.newTaskTitle,
                    onValueChange = { onEvent(MaintenanceEvent.OnNewTitleChange(it)) },
                    label = { Text("Título") },
                    placeholder = { Text("Ej. Cambio de aceite") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.newTaskDescription,
                    onValueChange = { onEvent(MaintenanceEvent.OnNewDescriptionChange(it)) },
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Agrega detalles si lo necesitas") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.newTaskDueMileage,
                    onValueChange = { onEvent(MaintenanceEvent.OnNewDueMileageChange(it)) },
                    label = { Text("Kilometraje objetivo (opcional)") },
                    placeholder = { Text("Ej. 150 000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Puedes dejar el kilometraje vacío si es un recordatorio por fecha.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onEvent(MaintenanceEvent.HideCreateSheet) },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = onCreateTask,
                enabled = state.newTaskTitle.isNotBlank(),
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Text("Guardar")
            }
        }
    }
}
