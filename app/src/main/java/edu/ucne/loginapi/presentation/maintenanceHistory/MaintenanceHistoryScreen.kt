@file:OptIn(ExperimentalMaterial3Api::class)
package edu.ucne.loginapi.presentation.maintenanceHistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.MaintenanceHistory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceHistoryScreen(
    viewModel: MaintenanceHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
                title = { Text(text = "Historial de mantenimiento") }
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
            CircularProgressIndicator(modifier = modifier)
        }
        state.currentCar == null -> {
            Text(
                text = "Configura un vehículo para ver el historial",
                modifier = modifier
            )
        }
        state.records.isEmpty() -> {
            Text(
                text = "No hay registros de mantenimiento",
                modifier = modifier
            )
        }
        else -> {
            MaintenanceHistoryList(
                records = state.records,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun MaintenanceHistoryList(
    records: List<MaintenanceHistory>,
    onEvent: (MaintenanceHistoryEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(records, key = { it.id }) { record ->
            MaintenanceHistoryItem(
                record = record,
                onDelete = { onEvent(MaintenanceHistoryEvent.OnDeleteRecord(record.id)) }
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

    Card(modifier = Modifier.fillMaxWidth()) {
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
                    contentDescription = "Eliminar"
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
            text = record.taskType.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyMedium
        )

        record.mileageKm?.let {
            Text(
                text = "$it km",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (!record.workshopName.isNullOrBlank()) {
            Text(
                text = record.workshopName,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (!costText.isNullOrBlank()) {
            Text(
                text = costText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
