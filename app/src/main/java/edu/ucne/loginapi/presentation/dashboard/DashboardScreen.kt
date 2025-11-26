@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import edu.ucne.loginapi.presentation.dashboard.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToMaintenance: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DashboardBody(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToMaintenance = onNavigateToMaintenance
    )
}

@Composable
fun DashboardBody(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onNavigateToMaintenance: () -> Unit
) {
    val snackState = remember { SnackbarHostState() }

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackState.showSnackbar(it)
            onEvent(DashboardEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.currentCar?.let { "${it.brand} ${it.model}" }
                            ?: "Mi vehículo",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
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
                    Text("Configura un vehículo para ver tu dashboard")
                }
            }

            else -> {
                DashboardContent(
                    state = state,
                    modifier = Modifier.padding(padding),
                    onNavigateToMaintenance = onNavigateToMaintenance
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    state: DashboardUiState,
    modifier: Modifier = Modifier,
    onNavigateToMaintenance: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Resumen general",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Próximas tareas: ${state.upcomingTasks.size}")
                    Text("Tareas vencidas: ${state.overdueTasks.size}")
                }
            }
        }

        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToMaintenance
            ) {
                Text("Gestionar Mantenimiento")
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "Siguientes tareas",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(state.upcomingTasks.take(3)) { task ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(task.title)
                    task.dueMileageKm?.let {
                        Text("A los $it km")
                    }
                }
            }
        }

        if (state.upcomingTasks.isEmpty()) {
            item {
                Text(
                    text = "No hay tareas próximas",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
