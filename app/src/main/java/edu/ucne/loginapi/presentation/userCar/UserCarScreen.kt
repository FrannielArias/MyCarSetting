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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(state.userMessage) {
        val message = state.userMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(UserCarEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Mis vehículos",
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(UserCarEvent.ShowCreateSheet) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar vehículo"
                )
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Cargando vehículos...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                state.cars.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "No hay vehículos registrados",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Presiona + para agregar tu primer vehículo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
    currentCarId: Int?,
    onSetCurrent: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCurrent) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isCurrent) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Año ${car.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                car.plate?.let {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Placa: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            IconButton(onClick = onSetCurrent) {
                Icon(
                    imageVector = if (isCurrent) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Seleccionar vehículo actual",
                    tint = if (isCurrent) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar vehículo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun NewCarSheet(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nuevo vehículo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        VehicleBrandDropdown(
            brands = state.brands,
            selectedBrandId = state.selectedBrandId,
            isLoading = state.isLoadingCatalog,
            onBrandSelected = { brand ->
                onEvent(UserCarEvent.OnBrandSelected(brand))
            }
        )

        VehicleModelDropdown(
            models = state.models,
            selectedModelId = state.selectedModelId,
            enabled = state.selectedBrandId != null && !state.isLoadingCatalog,
            onModelSelected = { model ->
                onEvent(UserCarEvent.OnModelSelected(model))
            }
        )

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
            onValueChange = { newValue ->
                if (newValue.length <= 7 && newValue.all { it.isLetterOrDigit() }) {
                    onEvent(UserCarEvent.OnPlateChange(newValue.uppercase()))
                }
            },
            label = { Text("Placa *") },
            placeholder = { Text("Ej: A123456") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.plate.isNotEmpty() && state.plate.length != 7,
            supportingText = {
                when {
                    state.plate.isEmpty() -> {
                        Text(
                            text = "Campo obligatorio - 7 caracteres",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.plate.length < 7 -> {
                        Text(
                            text = "${state.plate.length}/7 caracteres",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.plate.length == 7 -> {
                        Text(
                            text = "✓ Placa válida",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onEvent(UserCarEvent.OnSaveCar) },
            enabled = state.selectedBrandId != null &&
                    state.selectedModelId != null &&
                    state.selectedYearRangeId != null &&
                    state.plate.length == 7 && // Validación de placa
                    !state.isLoadingCatalog,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.isLoadingCatalog) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Guardar vehículo",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


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
                when {
                    isLoading -> Text("Cargando...")
                    brands.isEmpty() -> Text("No hay marcas disponibles")
                    else -> Text("Selecciona una marca")
                }
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
            enabled = !isLoading && brands.isNotEmpty(),
            singleLine = true
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
            enabled = enabled && models.isNotEmpty(),
            singleLine = true
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
            label = { Text("Año") },
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
            enabled = enabled && yearRanges.isNotEmpty(),
            singleLine = true
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

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "No hay vehículos registrados",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Presiona + para agregar tu primer vehículo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}