@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.maintenance

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator
import java.text.SimpleDateFormat
import java.util.Calendar
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
                    MyCarLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
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
    val overdueTasks = state.overdueTasks
    val overdueIds = overdueTasks.map { it.id }.toSet()
    val upcomingTasks = state.upcomingTasks.filter { it.id !in overdueIds }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (overdueTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Vencidas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(overdueTasks, key = { it.id }) { task ->
                MaintenanceTaskItem(
                    task = task,
                    isOverdue = true,
                    onComplete = { onEvent(MaintenanceEvent.OnCompleteTask(task.id)) },
                    onDelete = { onEvent(MaintenanceEvent.OnDeleteTask(task.id)) }
                )
            }
        }

        if (upcomingTasks.isNotEmpty()) {
            item {
                Text(
                    text = "Próximas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            items(upcomingTasks, key = { it.id }) { task ->
                MaintenanceTaskItem(
                    task = task,
                    isOverdue = false,
                    onComplete = { onEvent(MaintenanceEvent.OnCompleteTask(task.id)) },
                    onDelete = { onEvent(MaintenanceEvent.OnDeleteTask(task.id)) }
                )
            }
        }

        if (overdueTasks.isEmpty() && upcomingTasks.isEmpty() && !state.isLoading) {
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
                text = task.displayType(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val commonTitles = listOf(
        "Cambio de aceite",
        "Revisión de frenos",
        "Rotación de neumáticos",
        "Cambio de filtro de aire",
        "Revisión general"
    )

    val context = LocalContext.current
    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Nuevo recordatorio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = state.newTaskTitle,
            onValueChange = { onEvent(MaintenanceEvent.OnNewTitleChange(it)) },
            label = { Text("Título") },
            placeholder = { Text("Selecciona o escribe un mantenimiento") },
            modifier = Modifier.fillMaxWidth()
        )

        if (commonTitles.isNotEmpty()) {
            Text(
                text = "Tareas frecuentes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(commonTitles) { title ->
                    OutlinedButton(
                        onClick = { onEvent(MaintenanceEvent.OnNewTitleChange(title)) }
                    ) {
                        Text(text = title)
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.newTaskDescription,
            onValueChange = { onEvent(MaintenanceEvent.OnNewDescriptionChange(it)) },
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.newTaskDueMileage,
            onValueChange = { onEvent(MaintenanceEvent.OnNewDueMileageChange(it)) },
            label = { Text("Kilometraje objetivo (opcional)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.newTaskDueDateText,
            onValueChange = {},
            label = { Text("Fecha objetivo (opcional)") },
            placeholder = { Text("Selecciona una fecha") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (state.newTaskDueDateMillis != null) {
                    TextButton(onClick = { onEvent(MaintenanceEvent.OnClearNewDueDate) }) {
                        Text("Limpiar")
                    }
                }
            }
        )

        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val cal = Calendar.getInstance()
                        cal.set(year, month, dayOfMonth, 0, 0, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        val millis = cal.timeInMillis
                        val formatted = dateFormatter.format(Date(millis))
                        onEvent(MaintenanceEvent.OnNewDueDateSelected(millis, formatted))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar fecha")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onEvent(MaintenanceEvent.HideCreateSheet) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = onCreateTask,
                enabled = state.newTaskTitle.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

private fun MaintenanceTask.displayType(): String {
    return when (type.name) {
        "OIL_CHANGE" -> "Cambio de aceite"
        "TIRE_ROTATION" -> "Rotación de neumáticos"
        "BRAKE_INSPECTION" -> "Inspección de frenos"
        "GENERAL_CHECK" -> "Revisión general"
        else -> type.name
            .lowercase(Locale.getDefault())
            .replace('_', ' ')
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }
}
