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
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

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
                title = { Text("Mis vehículos", fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
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
                state.isLoading -> LoadingCars()
                state.cars.isEmpty() -> EmptyCarsState()
                else -> {
                    UserCarList(
                        cars = state.cars,
                        currentCarId = state.currentCarId,
                        onSetCurrent = { viewModel.onEvent(UserCarEvent.OnSetCurrentCar(it)) },
                        onDelete = { viewModel.onEvent(UserCarEvent.OnDeleteCar(it)) }
                    )
                }
            }
        }

        if (state.showCreateSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onEvent(UserCarEvent.HideCreateSheet) },
                sheetState = sheetState
            ) {
                NewCarSheet(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}

@Composable
private fun LoadingCars() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Cargando vehículos…")
        }
    }
}

@Composable
private fun EmptyCarsState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(64.dp))
            Text("No hay vehículos registrados")
            Text("Presiona + para agregar uno")
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                if (isCurrent) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(40.dp))

            Spacer(Modifier.size(16.dp))

            Column(Modifier.weight(1f)) {
                Text("${car.brand} ${car.model}", fontWeight = FontWeight.Bold)
                Text("Año ${car.year}")
                car.plate?.let { Text("Placa: $it") }
            }

            IconButton(onClick = onSetCurrent) {
                Icon(
                    if (isCurrent) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

/* ====================== NEW CAR SHEET ========================== */

@Composable
private fun NewCarSheet(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nuevo vehículo", fontWeight = FontWeight.Bold)

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

        PlateInput(
            plate = state.plate,
            onPlateChange = { onEvent(UserCarEvent.OnPlateChange(it)) }
        )

        SaveCarButton(state = state) {
            onEvent(UserCarEvent.OnSaveCar)
        }
    }
}

@Composable
private fun PlateInput(
    plate: String,
    onPlateChange: (String) -> Unit
) {
    OutlinedTextField(
        value = plate,
        onValueChange = { raw ->
            onPlateChange(sanitizePlateInput(raw))
        },
        label = { Text("Placa *") },
        singleLine = true,
        isError = plate.isNotEmpty() && plate.length != 7,
        supportingText = { PlateSupportingText(plate) },
        modifier = Modifier.fillMaxWidth()
    )
}

private fun sanitizePlateInput(input: String): String =
    input
        .take(7)
        .filter { it.isLetterOrDigit() }
        .uppercase()

@Composable
private fun PlateSupportingText(plate: String) {
    val (text, color) = when {
        plate.isEmpty() ->
            "Campo obligatorio - 7 caracteres" to MaterialTheme.colorScheme.error

        plate.length < 7 ->
            "${plate.length}/7 caracteres" to MaterialTheme.colorScheme.error

        else ->
            "✓ Placa válida" to MaterialTheme.colorScheme.primary
    }

    Text(text = text, color = color)
}

@Composable
private fun SaveCarButton(
    state: UserCarUiState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = canSaveCar(state),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (state.isLoadingCatalog) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text("Guardar vehículo")
        }
    }
}

private fun canSaveCar(state: UserCarUiState): Boolean {
    return state.selectedBrandId != null &&
            state.selectedModelId != null &&
            state.selectedYearRangeId != null &&
            state.plate.length == 7 &&
            !state.isLoadingCatalog
}

/* ====================== DROPDOWNS ========================== */

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
            if (!isLoading && brands.isNotEmpty()) {
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