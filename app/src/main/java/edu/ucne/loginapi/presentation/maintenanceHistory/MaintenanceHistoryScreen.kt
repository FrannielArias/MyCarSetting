@file:OptIn(ExperimentalMaterial3Api::class)
package edu.ucne.loginapi.presentation.maintenanceHistory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import edu.ucne.loginapi.domain.model.MaintenanceType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object MaintenanceConstants {
    const val GENERAL_CHECK = "Revisión general"
    const val AIR_FILTER = "Cambio de filtro de aire"

    val MAIN_FILTERS = listOf(
        "Cambio de aceite",
        "Revisión de frenos",
        "Rotación de neumáticos",
        AIR_FILTER,
        GENERAL_CHECK
    )
}


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
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(MaintenanceHistoryEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = state.currentCar?.let { "${it.brand} ${it.model}" }
                            ?: "Historial de mantenimiento",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                )
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Cargando historial...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        state.currentCar == null -> {
            Column(
                modifier = modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Selecciona un vehículo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Configura un vehículo para ver su historial de mantenimiento",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        state.records.isEmpty() -> {
            Column(
                modifier = modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Sin registros de mantenimiento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Cada vez que completes un mantenimiento, guarda el registro aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> {
            val filteredRecords = state.selectedFilterText?.let { filterText ->
                when (filterText) {
                    "Otros" -> {
                        state.records.filter { record ->
                            val text = record.notes?.trim() ?: ""
                            !MaintenanceConstants.MAIN_FILTERS.contains(text)
                        }
                    }

                    else -> {
                        state.records.filter {
                            it.notes?.trim().equals(filterText, ignoreCase = false)
                        }
                    }
                }
            } ?: state.records

            MaintenanceHistoryList(
                records = filteredRecords,
                allRecords = state.records,
                selectedFilterText = state.selectedFilterText,
                onSelectFilter = { onEvent(MaintenanceHistoryEvent.OnFilterTextSelected(it)) },
                onDelete = { id -> onEvent(MaintenanceHistoryEvent.OnDeleteRecord(id)) }
            )
        }
    }
}

@Composable
private fun MaintenanceHistoryList(
    records: List<MaintenanceHistory>,
    allRecords: List<MaintenanceHistory>,
    selectedFilterText: String?,
    onSelectFilter: (String?) -> Unit,
    onDelete: (Int) -> Unit
) {
    val monthFormatter = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    }

    val grouped = remember(records) {
        records.sortedByDescending { it.serviceDateMillis }
            .groupBy { monthFormatter.format(Date(it.serviceDateMillis)) }
    }

    val totalCost = allRecords.sumOf { it.cost ?: 0.0 }
    val totalRecords = allRecords.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            HistorySummaryCard(
                totalRecords = totalRecords,
                totalCost = totalCost,
                filteredCount = records.size,
                isFiltered = selectedFilterText != null
            )
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Filtrar por tipo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                CustomFilterRow(
                    selectedFilter = selectedFilterText,
                    onSelectFilter = onSelectFilter
                )
            }
        }

        if (records.isEmpty() && selectedFilterText != null) {
            item {
                EmptyCustomFilterState(selectedFilter = selectedFilterText)
            }
        } else {
            grouped.forEach { (month, list) ->
                item(key = "header_$month") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = month.replaceFirstChar { it.titlecase(Locale.getDefault()) },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "${list.size} ${if (list.size == 1) "registro" else "registros"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                itemsIndexed(
                    list,
                    key = { _, item -> item.id }
                ) { _, record ->
                    MaintenanceHistoryItem(
                        record = record,
                        onDelete = { onDelete(record.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCustomFilterState(selectedFilter: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterAlt,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "No hay registros de \"$selectedFilter\"",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Intenta con otro filtro",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CustomFilterRow(
    selectedFilter: String?,
    onSelectFilter: (String?) -> Unit
) {
    val filters = listOf(
        null to "Todos",
        "Cambio de aceite" to "Cambio de aceite",
        "Revisión de frenos" to "Revisión de frenos",
        "Rotación de neumáticos" to "Rotación de neumáticos",
        MaintenanceConstants.AIR_FILTER to MaintenanceConstants.AIR_FILTER,
        MaintenanceConstants.GENERAL_CHECK to MaintenanceConstants.GENERAL_CHECK,
        "Otros" to "Otros"
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { (value, label) ->
            val selected = selectedFilter == value
            FilterChip(
                selected = selected,
                onClick = { onSelectFilter(value) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun HistorySummaryCard(
    totalRecords: Int,
    totalCost: Double,
    filteredCount: Int,
    isFiltered: Boolean
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Resumen del historial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (isFiltered) {
                        Text(
                            text = "Mostrando $filteredCount de $totalRecords registros",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Total registros",
                    value = totalRecords.toString(),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(16.dp))
                SummaryItem(
                    label = "Inversión total",
                    value = currency,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun MaintenanceHistoryItem(
    record: MaintenanceHistory,
    onDelete: () -> Unit
) {
    val dateText = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date(record.serviceDateMillis))

    val costText = record.cost?.let { NumberFormat.getCurrencyInstance().format(it) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            RecordDetails(
                record = record,
                dateText = dateText,
                costText = costText,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar registro",
                    tint = MaterialTheme.colorScheme.error
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val title = record.notes?.takeIf { it.isNotBlank() }
            ?: record.taskType.displayName()

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            record.mileageKm?.let {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${it.toInt()} km",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (!record.workshopName.isNullOrBlank()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = record.workshopName!!,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!costText.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = costText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
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
        MaintenanceType.GENERAL_CHECK -> MaintenanceConstants.GENERAL_CHECK
        MaintenanceType.OTHER -> "Otro mantenimiento"
    }
}
