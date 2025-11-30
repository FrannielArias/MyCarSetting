@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.Services

import ServiceCategory
import ServiceItem
import ServicesUiState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator
import edu.ucne.loginapi.ui.components.ScreenScaffold

@Composable
fun ServicesScreen(
    viewModel: ServicesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ScreenScaffold(
        title = "Servicios cercanos"
    ) { padding, snackbarHostState ->
        LaunchedEffect(state.userMessage) {
            state.userMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.onEvent(ServicesEvent.OnUserMessageShown)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MyCarLoadingIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Buscando servicios cercanos...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ServicesContent(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}

@Composable
private fun ServicesContent(
    state: ServicesUiState,
    onEvent: (ServicesEvent) -> Unit
) {
    val services = remember(state.services, state.selectedCategory) {
        state.selectedCategory?.let { category ->
            state.services.filter { it.category == category }
        } ?: state.services
    }

    val defaultCenter = services.firstOrNull()?.let {
        LatLng(it.latitude, it.longitude)
    } ?: LatLng(18.4861, -69.9312)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCenter, 13f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            services.forEach { service ->
                Marker(
                    state = MarkerState(position = LatLng(service.latitude, service.longitude)),
                    title = service.name,
                    snippet = service.distanceText
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filtrar por tipo",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ServiceCategoryChips(
            selected = state.selectedCategory,
            onSelected = { category ->
                onEvent(ServicesEvent.OnCategorySelected(category))
            }
        )
    }

    Text(
        text = "Puntos de interés",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    if (services.isEmpty()) {
        Text(
            text = "No hay servicios para la categoría seleccionada.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(services, key = { it.id }) { service ->
                ServiceItemCard(service = service)
            }
        }
    }
}

@Composable
private fun ServiceCategoryChips(
    selected: ServiceCategory?,
    onSelected: (ServiceCategory?) -> Unit
) {
    val categories = listOf(
        null,
        ServiceCategory.TALLER,
        ServiceCategory.MANTENIMIENTO,
        ServiceCategory.LAVADO,
        ServiceCategory.EMERGENCIA
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = selected == category
            val label = when (category) {
                null -> "Todos"
                ServiceCategory.TALLER -> "Talleres"
                ServiceCategory.MANTENIMIENTO -> "Mantenimiento"
                ServiceCategory.LAVADO -> "Lavado"
                ServiceCategory.EMERGENCIA -> "Emergencia"
            }

            AssistChip(
                onClick = { onSelected(category) },
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
private fun ServiceItemCard(
    service: ServiceItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = service.distanceText,
                    style = MaterialTheme.typography.labelMedium
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (service.isOpen)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (service.isOpen) "Abierto" else "Cerrado",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color =
                            if (service.isOpen)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}