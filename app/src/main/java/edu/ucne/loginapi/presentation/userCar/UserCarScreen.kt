@file:OptIn(ExperimentalMaterial3Api::class)
package edu.ucne.loginapi.presentation.userCar

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.UserCar

@Composable
fun UserCarScreen(
    viewModel: UserCarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UserCarBody(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun UserCarBody(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(UserCarEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis vehículos") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEvent(UserCarEvent.ShowCreateSheet) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar vehículo")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            UserCarContent(state = state, onEvent = onEvent)

            if (state.showCreateSheet) {
                ModalBottomSheet(
                    onDismissRequest = { onEvent(UserCarEvent.HideCreateSheet) },
                    sheetState = sheetState
                ) {
                    UserCarCreateSheet(
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@Composable
private fun UserCarContent(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    when {
        state.isLoading -> {
            LoadingState()
        }
        state.cars.isEmpty() -> {
            EmptyState()
        }
        else -> {
            UserCarList(
                cars = state.cars,
                currentCarId = state.currentCarId,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Cargando vehículos...")
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No tienes vehículos registrados")
    }
}

@Composable
private fun UserCarList(
    cars: List<UserCar>,
    currentCarId: String?,
    onEvent: (UserCarEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cars, key = { it.id }) { car ->
            UserCarItem(
                car = car,
                isCurrent = currentCarId == car.id,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun UserCarItem(
    car: UserCar,
    isCurrent: Boolean,
    onEvent: (UserCarEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEvent(UserCarEvent.OnSetCurrentCar(car.id))
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CarDetails(
                car = car,
                isCurrent = isCurrent,
                modifier = Modifier.weight(1f)
            )
            CarActions(
                carId = car.id,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun CarDetails(
    car: UserCar,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "${car.brand} ${car.model}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Año ${car.year}",
            style = MaterialTheme.typography.bodyMedium
        )
        car.plate?.let {
            Text(
                text = "Placa: $it",
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (isCurrent) {
            Text(
                text = "Vehículo actual",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CarActions(
    carId: String,
    onEvent: (UserCarEvent) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                onEvent(UserCarEvent.OnSetCurrentCar(carId))
            }
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Seleccionar como actual"
            )
        }
        IconButton(
            onClick = {
                onEvent(UserCarEvent.OnDeleteCar(carId))
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar vehículo"
            )
        }
    }
}

@Composable
fun UserCarCreateSheet(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Nuevo vehículo",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = state.brand,
            onValueChange = { onEvent(UserCarEvent.OnBrandChange(it)) },
            label = { Text("Marca") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.model,
            onValueChange = { onEvent(UserCarEvent.OnModelChange(it)) },
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.yearText,
            onValueChange = { onEvent(UserCarEvent.OnYearChange(it)) },
            label = { Text("Año") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.plate,
            onValueChange = { onEvent(UserCarEvent.OnPlateChange(it)) },
            label = { Text("Placa (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onEvent(UserCarEvent.HideCreateSheet) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = { onEvent(UserCarEvent.OnSaveCar) },
                enabled = state.brand.isNotBlank() && state.model.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}
