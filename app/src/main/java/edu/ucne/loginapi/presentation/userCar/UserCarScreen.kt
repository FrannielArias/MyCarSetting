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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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

@Composable
private fun NewCarSheet(
    state: UserCarUiState,
    onEvent: (UserCarEvent) -> Unit
) {
    val brands = listOf("Toyota", "Honda", "Hyundai")
    val modelsByBrand = mapOf(
        "Toyota" to listOf("Camry", "Corolla", "Yaris"),
        "Honda" to listOf("Civic", "CRV", "Fit"),
        "Hyundai" to listOf("Elantra", "Tucson", "Accent")
    )
    val yearsByModel = mapOf(
        "Camry" to listOf("2007-2011", "2012-2017", "2018-2024"),
        "Corolla" to listOf("2006-2010", "2011-2015", "2016-2024"),
        "Yaris" to listOf("2008-2013", "2014-2020"),
        "Civic" to listOf("2006-2011", "2012-2016", "2017-2024"),
        "CRV" to listOf("2007-2011", "2012-2016", "2017-2024"),
        "Fit" to listOf("2008-2013", "2014-2020"),
        "Elantra" to listOf("2007-2012", "2013-2017", "2018-2024"),
        "Tucson" to listOf("2009-2013", "2014-2018", "2019-2024"),
        "Accent" to listOf("2006-2011", "2012-2017", "2018-2023")
    )

    val availableModels = modelsByBrand[state.brand].orEmpty()
    val availableYearRanges = yearsByModel[state.model].orEmpty()

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

        BrandDropdownField(
            selectedBrand = state.brand,
            brands = brands,
            onBrandSelected = { brand ->
                onEvent(UserCarEvent.OnBrandChange(brand))
                onEvent(UserCarEvent.OnModelChange(""))
                onEvent(UserCarEvent.OnYearChange(""))
            }
        )

        ModelDropdownField(
            selectedModel = state.model,
            models = availableModels,
            enabled = brands.isNotEmpty() && state.brand.isNotBlank(),
            onModelSelected = { model ->
                onEvent(UserCarEvent.OnModelChange(model))
                onEvent(UserCarEvent.OnYearChange(""))
            }
        )

        YearRangeDropdownField(
            selectedRange = state.yearText,
            yearRanges = availableYearRanges,
            enabled = availableModels.isNotEmpty() && state.model.isNotBlank(),
            onYearRangeSelected = { range ->
                onEvent(UserCarEvent.OnYearChange(range))
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
                enabled = state.brand.isNotBlank() &&
                        state.model.isNotBlank() &&
                        state.yearText.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun BrandDropdownField(
    selectedBrand: String,
    brands: List<String>,
    onBrandSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (brands.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedBrand,
            onValueChange = {},
            readOnly = true,
            label = { Text("Marca") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            brands.forEach { brand ->
                DropdownMenuItem(
                    text = { Text(brand) },
                    onClick = {
                        expanded = false
                        onBrandSelected(brand)
                    }
                )
            }
        }
    }
}

@Composable
private fun ModelDropdownField(
    selectedModel: String,
    models: List<String>,
    enabled: Boolean,
    onModelSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled && models.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedModel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Modelo") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model) },
                    onClick = {
                        expanded = false
                        onModelSelected(model)
                    }
                )
            }
        }
    }
}

@Composable
private fun YearRangeDropdownField(
    selectedRange: String,
    yearRanges: List<String>,
    enabled: Boolean,
    onYearRangeSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled && yearRanges.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedRange,
            onValueChange = {},
            readOnly = true,
            label = { Text("Año de caja") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            yearRanges.forEach { range ->
                DropdownMenuItem(
                    text = { Text(range) },
                    onClick = {
                        expanded = false
                        onYearRangeSelected(range)
                    }
                )
            }
        }
    }
}
