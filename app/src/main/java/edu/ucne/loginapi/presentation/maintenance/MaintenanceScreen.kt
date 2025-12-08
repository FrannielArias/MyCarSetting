@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.maintenance

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.MaintenanceSeverity
import edu.ucne.loginapi.domain.model.MaintenanceTask
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceScreen(
    viewModel: MaintenanceViewModel = hiltViewModel(),
    focusedTaskId: Int? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(MaintenanceEvent.Refresh)
    }

    MaintenanceBody(
        state = state,
        focusedTaskId = focusedTaskId,
        onEvent = viewModel::onEvent,
        onCreateTask = { viewModel.createTask() }
    )
}

@Composable
fun MaintenanceBody(
    state: MaintenanceUiState,
    focusedTaskId: Int? = null,
    onEvent: (MaintenanceEvent) -> Unit,
    onCreateTask: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    MaintenanceUserMessages(
        userMessage = state.userMessage,
        snackbarHostState = snackbarHostState,
        onEvent = onEvent
    )

    Scaffold(
        topBar = {
            MaintenanceTopBar(
                title = state.currentCar?.let { "${it.brand} ${it.model}" } ?: "Mantenimiento"
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            MaintenanceFab(onClick = { onEvent(MaintenanceEvent.ShowCreateSheet) })
        }
    ) { padding ->
        MaintenanceScaffoldContent(
            state = state,
            focusedTaskId = focusedTaskId,
            onEvent = onEvent,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )

        if (state.showCreateSheet) {
            MaintenanceBottomSheetHost(
                showSheet = state.showCreateSheet,
                sheetState = sheetState,
                state = state,
                onEvent = onEvent,
                onCreateTask = onCreateTask
            )
        }

        if (state.showCompleteTaskDialog) {
            CompleteTaskDialog(state = state, onEvent = onEvent)
        }
    }
}

@Composable
fun CompleteTaskDialog(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "DO")).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 2
        }
    }

    AlertDialog(
        onDismissRequest = { onEvent(MaintenanceEvent.HideCompleteTaskDialog) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CompleteTaskHeader()
                Divider()
                CompleteTaskCostInput(state = state, onEvent = onEvent)
                CompleteTaskCostPreview(state = state, formatter = currencyFormatter)
                CompleteTaskInfoCard()
                CompleteTaskActions(state = state, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun CompleteTaskHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Column {
            Text(
                text = "Completar Tarea",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Registra el costo del servicio (opcional)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompleteTaskCostInput(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit
) {
    OutlinedTextField(
        value = state.completeCostAmount,
        onValueChange = { value ->
            val filtered = value.filter { it.isDigit() || it == '.' }
            onEvent(MaintenanceEvent.OnCostAmountChange(filtered))
        },
        label = { Text("Costo del servicio (opcional)") },
        placeholder = { Text("0.00") },
        leadingIcon = {
            Text(
                text = "$",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (state.completeCostAmount.isNotBlank()) {
                IconButton(onClick = { onEvent(MaintenanceEvent.OnCostAmountChange("")) }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = state.completeCostError != null,
        supportingText = {
            if (state.completeCostError != null) {
                Text(
                    text = state.completeCostError,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = "Puedes dejar este campo vacío si no hubo costo",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CompleteTaskCostPreview(
    state: MaintenanceUiState,
    formatter: NumberFormat
) {
    if (state.completeCostAmount.isBlank() || state.completeCostError != null) return

    val cost = state.completeCostAmount.toDoubleOrNull() ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total a registrar:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = formatter.format(cost),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CompleteTaskInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Esta acción marcará la tarea como completada y guardará el historial de gastos de tu vehículo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompleteTaskActions(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = { onEvent(MaintenanceEvent.HideCompleteTaskDialog) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancelar")
        }
        Button(
            onClick = { onEvent(MaintenanceEvent.ConfirmCompleteTask) },
            enabled = state.completeCostError == null,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("Completar")
        }
    }
}

@Composable
private fun MaintenanceUserMessages(
    userMessage: String?,
    snackbarHostState: SnackbarHostState,
    onEvent: (MaintenanceEvent) -> Unit
) {
    LaunchedEffect(userMessage) {
        val message = userMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(MaintenanceEvent.OnUserMessageShown)
    }
}

@Composable
private fun MaintenanceTopBar(title: String) {
    TopAppBar(title = { Text(text = title, style = MaterialTheme.typography.titleLarge) })
}

@Composable
private fun MaintenanceFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar recordatorio")
    }
}

@Composable
private fun MaintenanceScaffoldContent(
    state: MaintenanceUiState,
    focusedTaskId: Int?,
    onEvent: (MaintenanceEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        when {
            state.isLoading -> {
                MyCarLoadingIndicator(modifier = Modifier.align(Alignment.Center))
            }

            state.currentCar == null -> {
                MaintenanceNoCarPlaceholder(modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                MaintenanceContent(state = state, focusedTaskId = focusedTaskId, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun MaintenanceNoCarPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = "Configura un vehículo para ver recordatorios",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun MaintenanceBottomSheetHost(
    showSheet: Boolean,
    sheetState: SheetState,
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit,
    onCreateTask: () -> Unit
) {
    if (!showSheet) return

    ModalBottomSheet(
        onDismissRequest = { onEvent(MaintenanceEvent.HideCreateSheet) },
        sheetState = sheetState
    ) {
        MaintenanceCreateSheet(state = state, onEvent = onEvent, onCreateTask = onCreateTask)
    }
}

@Composable
fun MaintenanceContent(
    state: MaintenanceUiState,
    focusedTaskId: Int?,
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
        item {
            MaintenanceSummaryBanner(
                overdueCount = overdueTasks.size,
                upcomingCount = upcomingTasks.size
            )
        }

        overdueTasksSection(
            overdueTasks = overdueTasks,
            focusedTaskId = focusedTaskId,
            onEvent = onEvent
        )

        upcomingTasksSection(
            upcomingTasks = upcomingTasks,
            focusedTaskId = focusedTaskId,
            onEvent = onEvent
        )

        emptyTasksSection(
            hasOverdue = overdueTasks.isNotEmpty(),
            hasUpcoming = upcomingTasks.isNotEmpty(),
            isLoading = state.isLoading
        )
    }
}

private fun LazyListScope.overdueTasksSection(
    overdueTasks: List<MaintenanceTask>,
    focusedTaskId: Int?,
    onEvent: (MaintenanceEvent) -> Unit
) {
    if (overdueTasks.isEmpty()) return

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
            isFocused = task.id == focusedTaskId,
            onComplete = { onEvent(MaintenanceEvent.ShowCompleteTaskDialog(task.id)) },
            onDelete = { onEvent(MaintenanceEvent.OnDeleteTask(task.id)) }
        )
    }
}

private fun LazyListScope.upcomingTasksSection(
    upcomingTasks: List<MaintenanceTask>,
    focusedTaskId: Int?,
    onEvent: (MaintenanceEvent) -> Unit
) {
    if (upcomingTasks.isEmpty()) return

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
            isFocused = task.id == focusedTaskId,
            onComplete = { onEvent(MaintenanceEvent.ShowCompleteTaskDialog(task.id)) },
            onDelete = { onEvent(MaintenanceEvent.OnDeleteTask(task.id)) }
        )
    }
}

private fun LazyListScope.emptyTasksSection(
    hasOverdue: Boolean,
    hasUpcoming: Boolean,
    isLoading: Boolean
) {
    if (hasOverdue || hasUpcoming || isLoading) return

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

@Composable
private fun MaintenanceSummaryBanner(overdueCount: Int, upcomingCount: Int) {
    val ui = summaryUi(overdueCount, upcomingCount)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ui.container,
            contentColor = ui.content
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ui.content.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ui.icon,
                    contentDescription = null,
                    tint = ui.content
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = ui.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ui.content
                )
                Text(
                    text = ui.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = ui.content.copy(alpha = 0.9f)
                )
            }
        }
    }
}

private data class SummaryUi(
    val container: Color,
    val content: Color,
    val icon: ImageVector,
    val title: String,
    val message: String
)

@Composable
private fun summaryUi(overdueCount: Int, upcomingCount: Int): SummaryUi {
    return when {
        overdueCount > 0 -> SummaryUi(
            container = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer,
            icon = Icons.Filled.Warning,
            title = "Tienes $overdueCount tareas vencidas",
            message = "Te recomendamos atender al menos una esta semana para evitar problemas en tu vehículo."
        )

        upcomingCount > 0 -> SummaryUi(
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer,
            icon = Icons.Filled.Info,
            title = "Tienes $upcomingCount tareas próximas",
            message = "Si las completas a tiempo, mantendrás tu vehículo en buen estado y evitarás fallas futuras."
        )

        else -> SummaryUi(
            container = MaterialTheme.colorScheme.surfaceVariant,
            content = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = Icons.Filled.Check,
            title = "Todo al día",
            message = "No tienes tareas pendientes. Mantén tus registros actualizados para seguir así."
        )
    }
}

@Composable
fun MaintenanceTaskItem(
    task: MaintenanceTask,
    isOverdue: Boolean,
    isFocused: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = taskColors(isOverdue, isFocused)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.container,
            contentColor = colors.content
        ),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.content,
                    modifier = Modifier.weight(1f)
                )
                val statusLabel = if (isOverdue) "Vencida" else "Próxima"
                val statusColor =
                    if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                InfoTag(
                    text = statusLabel,
                    background = statusColor.copy(alpha = 0.12f),
                    contentColor = statusColor
                )
            }

            if (!task.description.isNullOrBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.content.copy(alpha = 0.9f)
                )
            }

            TaskDetails(
                task = task,
                isOverdue = isOverdue,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onComplete) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Marcar completada",
                        tint = colors.content
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = colors.content
                    )
                }
            }
        }
    }
}

private data class TaskColors(val container: Color, val content: Color)

@Composable
private fun taskColors(isOverdue: Boolean, isFocused: Boolean): TaskColors {
    return when {
        isFocused -> TaskColors(
            container = MaterialTheme.colorScheme.primaryContainer,
            content = MaterialTheme.colorScheme.onPrimaryContainer
        )

        isOverdue -> TaskColors(
            container = MaterialTheme.colorScheme.errorContainer,
            content = MaterialTheme.colorScheme.onErrorContainer
        )

        else -> TaskColors(
            container = MaterialTheme.colorScheme.surfaceVariant,
            content = MaterialTheme.colorScheme.onSurface
        )
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoTag(
                text = task.displayType(),
                background = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (dateText != null) {
                InfoTag(
                    text = dateText,
                    background = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val severityColor = severityColor(task.severity)
            InfoTag(
                text = task.severityLabel(),
                background = severityColor.copy(alpha = 0.12f),
                contentColor = severityColor
            )
        }

        if (task.dueMileageKm != null) {
            val textColor =
                if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Próximo a los ${task.dueMileageKm} km",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}

@Composable
private fun severityColor(severity: MaintenanceSeverity): Color {
    return when (severity) {
        MaintenanceSeverity.LOW -> MaterialTheme.colorScheme.primary
        MaintenanceSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiary
        MaintenanceSeverity.HIGH -> MaterialTheme.colorScheme.error
        MaintenanceSeverity.CRITICAL -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun InfoTag(text: String, background: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
    val dateTimeFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CreateSheetTitle()
        TitleInput(title = state.newTaskTitle, error = state.newTitleError, onEvent = onEvent)
        CommonTitlesSection(titles = commonTitles, onEvent = onEvent)
        SeveritySection(selectedSeverity = state.newTaskSeverity, onEvent = onEvent)
        DescriptionAndMileageSection(
            description = state.newTaskDescription,
            mileage = state.newTaskDueMileage,
            onEvent = onEvent
        )
        DueDateReadOnlyField(dateText = state.newTaskDueDateText)
        DueDatePickerButton(
            currentMillis = state.newTaskDueDateMillis,
            formatter = dateTimeFormatter,
            context = context,
            onEvent = onEvent
        )
        Spacer(modifier = Modifier.height(8.dp))
        CreateSheetActions(state = state, onEvent = onEvent, onCreateTask = onCreateTask)
    }
}

@Composable
private fun CreateSheetTitle() {
    Text(
        text = "Nuevo recordatorio",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun TitleInput(
    title: String,
    error: String?,
    onEvent: (MaintenanceEvent) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = { onEvent(MaintenanceEvent.OnNewTitleChange(it)) },
        label = { Text("Título") },
        placeholder = { Text("Selecciona o escribe un mantenimiento") },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CommonTitlesSection(
    titles: List<String>,
    onEvent: (MaintenanceEvent) -> Unit
) {
    if (titles.isEmpty()) {
        Text(
            text = "Tareas frecuentes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(titles) { title ->
            OutlinedButton(onClick = { onEvent(MaintenanceEvent.OnNewTitleChange(title)) }) {
                Text(text = title)
            }
        }
    }
}

@Composable
private fun SeveritySection(
    selectedSeverity: MaintenanceSeverity,
    onEvent: (MaintenanceEvent) -> Unit
) {
    Text(
        text = "Nivel de gravedad",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SeverityChip(
            label = "Baja",
            selected = selectedSeverity == MaintenanceSeverity.LOW,
            onClick = { onEvent(MaintenanceEvent.OnNewSeveritySelected(MaintenanceSeverity.LOW)) }
        )
        SeverityChip(
            label = "Media",
            selected = selectedSeverity == MaintenanceSeverity.MEDIUM,
            onClick = { onEvent(MaintenanceEvent.OnNewSeveritySelected(MaintenanceSeverity.MEDIUM)) }
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SeverityChip(
            label = "Alta",
            selected = selectedSeverity == MaintenanceSeverity.HIGH,
            onClick = { onEvent(MaintenanceEvent.OnNewSeveritySelected(MaintenanceSeverity.HIGH)) }
        )
        SeverityChip(
            label = "Crítica",
            selected = selectedSeverity == MaintenanceSeverity.CRITICAL,
            onClick = { onEvent(MaintenanceEvent.OnNewSeveritySelected(MaintenanceSeverity.CRITICAL)) }
        )
    }
}

@Composable
private fun DescriptionAndMileageSection(
    description: String,
    mileage: String,
    onEvent: (MaintenanceEvent) -> Unit
) {
    OutlinedTextField(
        value = description,
        onValueChange = { onEvent(MaintenanceEvent.OnNewDescriptionChange(it)) },
        label = { Text("Descripción (opcional)") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = mileage,
        onValueChange = { onEvent(MaintenanceEvent.OnNewDueMileageChange(it)) },
        label = { Text("Kilometraje objetivo (opcional)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DueDateReadOnlyField(dateText: String) {
    OutlinedTextField(
        value = dateText,
        onValueChange = {},
        label = { Text("Fecha y hora objetivo (obligatoria)") },
        placeholder = { Text("Selecciona una fecha y hora") },
        readOnly = true,
        enabled = false,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DueDatePickerButton(
    currentMillis: Long?,
    formatter: SimpleDateFormat,
    context: Context,
    onEvent: (MaintenanceEvent) -> Unit
) {
    Button(
        onClick = {
            showDateTimePicker(
                context = context,
                currentMillis = currentMillis,
                formatter = formatter
            ) { millis, formatted ->
                onEvent(MaintenanceEvent.OnNewDueDateSelected(millis, formatted))
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Seleccionar fecha y hora")
    }
}

private fun showDateTimePicker(
    context: Context,
    currentMillis: Long?,
    formatter: SimpleDateFormat,
    onSelected: (Long, String) -> Unit
) {
    val baseCal = Calendar.getInstance().apply {
        if (currentMillis != null) timeInMillis = currentMillis
    }
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val dateCal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    dateCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    dateCal.set(Calendar.MINUTE, minute)
                    dateCal.set(Calendar.SECOND, 0)
                    dateCal.set(Calendar.MILLISECOND, 0)
                    val millis = dateCal.timeInMillis
                    val formatted = formatter.format(Date(millis))
                    onSelected(millis, formatted)
                },
                baseCal.get(Calendar.HOUR_OF_DAY),
                baseCal.get(Calendar.MINUTE),
                true
            ).show()
        },
        baseCal.get(Calendar.YEAR),
        baseCal.get(Calendar.MONTH),
        baseCal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
private fun CreateSheetActions(
    state: MaintenanceUiState,
    onEvent: (MaintenanceEvent) -> Unit,
    onCreateTask: () -> Unit
) {
    val canSave =
        state.newTaskTitle.length >= 5 &&
                state.newTitleError == null &&
                state.newTaskDueDateMillis != null
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            onClick = { onEvent(MaintenanceEvent.HideCreateSheet) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancelar")
        }
        Button(
            onClick = onCreateTask,
            enabled = canSave,
            modifier = Modifier.weight(1f)
        ) {
            Text("Guardar")
        }
    }
}

@Composable
private fun SeverityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        FilledTonalButton(onClick = onClick) {
            Text(text = label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(text = label)
        }
    }
}

private fun MaintenanceTask.displayType(): String {
    return when (type.name) {
        "OIL_CHANGE" -> "Cambio de aceite"
        "TIRE_ROTATION" -> "Rotación de neumáticos"
        "BRAKE_SERVICE" -> "Servicio de frenos"
        "GENERAL_CHECK" -> "Revisión general"
        else -> type.name
            .lowercase(Locale.getDefault())
            .replace('_', ' ')
            .replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }
}

private fun MaintenanceTask.severityLabel(): String {
    return when (severity) {
        MaintenanceSeverity.LOW -> "Baja"
        MaintenanceSeverity.MEDIUM -> "Media"
        MaintenanceSeverity.HIGH -> "Alta"
        MaintenanceSeverity.CRITICAL -> "Crítica"
    }
}