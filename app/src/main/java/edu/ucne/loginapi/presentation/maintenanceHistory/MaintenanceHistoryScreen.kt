@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.maintenanceHistory

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceHistoryScreen(
    viewModel: MaintenanceHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(MaintenanceHistoryEvent.Refresh)
    }

    MaintenanceHistoryBody(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun MaintenanceHistoryBody(
    state: MaintenanceHistoryUiState,
    onEvent: (MaintenanceHistoryEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(MaintenanceHistoryEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.currentCar?.let { "Historial · ${it.brand} ${it.model}" }
                            ?: "Historial de mantenimiento",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            MaintenanceHistoryContent(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun MaintenanceHistoryContent(
    state: MaintenanceHistoryUiState,
    onEvent: (MaintenanceHistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator()
                Text(
                    text = "Cargando historial...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        state.currentCar == null -> {
            Text(
                text = "Configura un vehículo para ver el historial",
                modifier = modifier,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        state.records.isEmpty() -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No hay registros de mantenimiento",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Cada vez que completes un mantenimiento, guarda el registro en la app.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            val filteredRecords = state.selectedType?.let { type ->
                state.records.filter { it.taskType == type }
            } ?: state.records

            MaintenanceHistoryList(
                records = filteredRecords,
                selectedType = state.selectedType,
                onSelectType = { type ->
                    onEvent(MaintenanceHistoryEvent.OnTypeFilterSelected(type))
                },
                onDelete = { id ->
                    onEvent(MaintenanceHistoryEvent.OnDeleteRecord(id))
                }
            )
        }
    }
}

@Composable
private fun MaintenanceHistoryList(
    records: List<MaintenanceHistory>,
    selectedType: MaintenanceType?,
    onSelectType: (MaintenanceType?) -> Unit,
    onDelete: (String) -> Unit
) {
    val monthFormatter = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    }

    val grouped = remember(records) {
        records.sortedByDescending { it.serviceDateMillis }
            .groupBy { monthFormatter.format(Date(it.serviceDateMillis)) }
    }

    val totalCost = records.sumOf { it.cost ?: 0.0 }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HistorySummaryCard(
                totalRecords = records.size,
                totalCost = totalCost
            )
        }

        item {
            TypeFilterRow(
                selectedType = selectedType,
                onSelectType = onSelectType
            )
        }

        grouped.forEach { (month, list) ->
            item(key = "header_$month") {
                Text(
                    text = month.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp)
                )
            }

            itemsIndexed(list, key = { _, item -> item.id }) { _, record ->
                MaintenanceHistoryItem(
                    record = record,
                    onDelete = { onDelete(record.id) }
                )
            }
        }
    }
}

@Composable
private fun HistorySummaryCard(
    totalRecords: Int,
    totalCost: Double
) {
    val currency = NumberFormat.getCurrencyInstance().format(totalCost)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Resumen del historial",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Registros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = totalRecords.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Costo total aprox.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currency,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeFilterRow(
    selectedType: MaintenanceType?,
    onSelectType: (MaintenanceType?) -> Unit
) {
    val types = listOf<MaintenanceType?>(null) +
            listOf(
                MaintenanceType.OIL_CHANGE,
                MaintenanceType.BRAKE_SERVICE,
                MaintenanceType.TIRE_ROTATION,
                MaintenanceType.GENERAL_CHECK,
                MaintenanceType.OTHER
            )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        types.forEach { type ->
            val isSelected = selectedType == type
            val label = type?.displayName() ?: "Todos"

            AssistChip(
                onClick = { onSelectType(type) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor =
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                    labelColor =
                        if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun MaintenanceHistoryItem(
    record: MaintenanceHistory,
    onDelete: () -> Unit
) {
    val dateText = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date(record.serviceDateMillis))

    val costText = record.cost?.let {
        NumberFormat.getCurrencyInstance().format(it)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecordDetails(
                record = record,
                dateText = dateText,
                costText = costText,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar registro"
                )
            }
        }
    }
}

@Composable
private fun RecordDetails(
    record: MaintenanceHistory,
    dateText: String,
    costText: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = record.taskType.displayName(),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            record.mileageKm?.let {
                Text(
                    text = "$it km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!record.workshopName.isNullOrBlank()) {
                Text(
                    text = record.workshopName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (!record.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = record.notes,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!costText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = costText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun MaintenanceType.displayName(): String {
    return when (this) {
        MaintenanceType.OIL_CHANGE -> "Cambio de aceite"
        MaintenanceType.FILTER -> "Cambio de filtro"
        MaintenanceType.BRAKE_SERVICE -> "Servicio de frenos"
        MaintenanceType.TIRE_ROTATION -> "Rotación de neumáticos"
        MaintenanceType.TIRE_CHANGE -> "Cambio de neumáticos"
        MaintenanceType.ALIGNMENT -> "Alineación"
        MaintenanceType.BATTERY -> "Batería"
        MaintenanceType.COOLANT -> "Refrigerante"
        MaintenanceType.INSURANCE_RENEWAL -> "Renovación de seguro"
        MaintenanceType.TAX_RENEWAL -> "Renovación de impuestos"
        MaintenanceType.GENERAL_CHECK -> "Revisión general"
        MaintenanceType.OTHER -> "Otro mantenimiento"
    }
}