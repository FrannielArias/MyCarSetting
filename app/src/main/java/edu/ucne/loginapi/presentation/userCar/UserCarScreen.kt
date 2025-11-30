@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.userCar

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.loginapi.domain.model.UserCar
import edu.ucne.loginapi.domain.model.VehicleBrand
import edu.ucne.loginapi.domain.model.VehicleModel
import edu.ucne.loginapi.domain.model.VehicleYearRange

@Composable
fun UserCarScreen(
    viewModel: UserCarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.userMessage) {
        val message = state.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(UserCarEvent.OnUserMessageShown)
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
            FloatingActionButton(
                onClick = { viewModel.onEvent(UserCarEvent.ShowCreateSheet) }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar vehículo")
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
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando vehículos...")
                    }
                }

                state.cars.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay vehículos registrados")
                    }
                }

                else -> {
                    UserCarList(
                        cars = state.cars,
                        currentCarId = state.currentCarId,
                        onSetCurrent = { id ->
                            viewModel.onEvent(UserCarEvent.OnSetCurrentCar(id))
                        },
                        onDelete = { id ->
                            viewModel.onEvent(UserCarEvent.OnDeleteCar(id))
                        }
                    )
                }
            }
        }

        if (state.showCreateSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.onEvent(UserCarEvent.HideCreateSheet)
                },
                sheetState = sheetState
            ) {
                NewCarSheet(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
private fun UserCarList(
    cars: List<UserCar>,
    currentCarId: String?,
    onSetCurrent: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cars, key = { it.id }) { car ->
            UserCarItem(
                car = car,
                isCurrent = car.id == currentCarId,
                onSetCurrent = { onSetCurrent(car.id) },
                onDelete = { onDelete(car.id) }
            )
        }
    }
}

@Composable
private fun UserCarItem(
    car: UserCar,
    isCurrent: Boolean,
    onSetCurrent: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = car.year.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
                car.plate?.let {
                    Text(
                        text = "Placa: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onSetCurrent) {
                Icon(
                    imageVector = if (isCurrent) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Seleccionar vehículo actual"
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar vehículo"
                )
            }
        }
    }
}

// ========================================
// NUEVO SHEET CON DATOS DE API
// ========================================
@Composable
private fun NewCarSheet(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Nuevo vehículo",
            style = MaterialTheme.typography.titleLarge
        )

        // ✅ Selector de Marca con datos reales de la API
        VehicleBrandDropdown(
            brands = state.brands,
            selectedBrandId = state.selectedBrandId,
            isLoading = state.isLoadingCatalog,
            onBrandSelected = { brand ->
                onEvent(UserCarEvent.OnBrandSelected(brand))
            }
        )

        // ✅ Selector de Modelo (habilitado solo si hay una marca seleccionada)
        VehicleModelDropdown(
            models = state.models,
            selectedModelId = state.selectedModelId,
            enabled = state.selectedBrandId != null && !state.isLoadingCatalog,
            onModelSelected = { model ->
                onEvent(UserCarEvent.OnModelSelected(model))
            }
        )

        // ✅ Selector de Año (habilitado solo si hay un modelo seleccionado)
        VehicleYearRangeDropdown(
            yearRanges = state.yearRanges,
            selectedYearRangeId = state.selectedYearRangeId,
            enabled = state.selectedModelId != null && !state.isLoadingCatalog,
            onYearRangeSelected = { yearRange ->
                onEvent(UserCarEvent.OnYearRangeSelected(yearRange))
            }
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
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onEvent(UserCarEvent.OnSaveCar) },
                enabled = state.selectedBrandId != null &&
                        state.selectedModelId != null &&
                        state.selectedYearRangeId != null &&
                        !state.isLoadingCatalog,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isLoadingCatalog) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
    }
}

// ========================================
// DROPDOWNS ACTUALIZADOS CON DATOS REALES
// ========================================

@Composable
private fun VehicleBrandDropdown(
    brands: List<VehicleBrand>,
    selectedBrandId: Int?,
    isLoading: Boolean,
    onBrandSelected: (VehicleBrand) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedBrand = brands.find { it.id == selectedBrandId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (brands.isNotEmpty() && !isLoading) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedBrand?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Marca") },
            placeholder = {
                if (isLoading) Text("Cargando...")
                else if (brands.isEmpty()) Text("No hay marcas disponibles")
                else Text("Selecciona una marca")
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            enabled = !isLoading && brands.isNotEmpty()
        )

        if (brands.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                brands.forEach { brand ->
                    DropdownMenuItem(
                        text = { Text(brand.name) },
                        onClick = {
                            expanded = false
                            onBrandSelected(brand)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleModelDropdown(
    models: List<VehicleModel>,
    selectedModelId: Int?,
    enabled: Boolean,
    onModelSelected: (VehicleModel) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedModel = models.find { it.id == selectedModelId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled && models.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedModel?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Modelo") },
            placeholder = {
                when {
                    !enabled -> Text("Primero selecciona una marca")
                    models.isEmpty() -> Text("No hay modelos disponibles")
                    else -> Text("Selecciona un modelo")
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled && models.isNotEmpty()
        )

        if (models.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                models.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model.name) },
                        onClick = {
                            expanded = false
                            onModelSelected(model)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleYearRangeDropdown(
    yearRanges: List<VehicleYearRange>,
    selectedYearRangeId: Int?,
    enabled: Boolean,
    onYearRangeSelected: (VehicleYearRange) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedYearRange = yearRanges.find { it.id == selectedYearRangeId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled && yearRanges.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedYearRange?.let { "${it.fromYear} - ${it.toYear}" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Año de caja") },
            placeholder = {
                when {
                    !enabled -> Text("Primero selecciona un modelo")
                    yearRanges.isEmpty() -> Text("No hay años disponibles")
                    else -> Text("Selecciona un rango de años")
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled && yearRanges.isNotEmpty()
        )

        if (yearRanges.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                yearRanges.forEach { yearRange ->
                    DropdownMenuItem(
                        text = { Text("${yearRange.fromYear} - ${yearRange.toYear}") },
                        onClick = {
                            expanded = false
                            onYearRangeSelected(yearRange)
                        }
                    )
                }
            }
        }
    }
}