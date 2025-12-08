@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.Services

import ServiceCategory
import ServiceItem
import ServicesUiState
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator
import edu.ucne.loginapi.ui.components.ScreenScaffold
import android.util.Log
import androidx.compose.material3.*

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
            ServicesContent(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun ServicesContent(
    state: ServicesUiState,
    onEvent: (ServicesEvent) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var hasLocationPermission by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoadingLocation by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            Log.d("ServicesScreen", "✅ Permisos concedidos, obteniendo ubicación...")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    myLocation = latLng
                    isLoadingLocation = false
                    Log.d("ServicesScreen", "✅ Ubicación obtenida: ${location.latitude}, ${location.longitude}")

                    // ✅ LLAMAR AL VIEWMODEL CON LA UBICACIÓN
                    onEvent(ServicesEvent.LoadForLocation(location.latitude, location.longitude))
                } else {
                    Log.e("ServicesScreen", "❌ Location es null")
                    isLoadingLocation = false
                }
            }.addOnFailureListener { e ->
                Log.e("ServicesScreen", "❌ Error al obtener ubicación: ${e.message}")
                isLoadingLocation = false
            }
        } else {
            Log.e("ServicesScreen", "❌ Permisos NO concedidos")
            isLoadingLocation = false
        }
    }

    LaunchedEffect(Unit) {
        Log.d("ServicesScreen", "🔄 Solicitando permisos de ubicación...")
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    if (isLoadingLocation || state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyCarLoadingIndicator()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isLoadingLocation) "Obteniendo tu ubicación..." else "Buscando servicios cercanos...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    if (!hasLocationPermission) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Se requiere permiso de ubicación",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Necesitamos tu ubicación para mostrarte servicios cercanos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }) {
                Text("Conceder permiso")
            }
        }
        return
    }

    if (myLocation == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No se pudo obtener tu ubicación",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Asegúrate de tener el GPS activado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                isLoadingLocation = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        myLocation = latLng
                        onEvent(ServicesEvent.LoadForLocation(location.latitude, location.longitude))
                    }
                    isLoadingLocation = false
                }
            }) {
                Text("Reintentar")
            }
        }
        return
    }

    val services = remember(state.services, state.selectedCategory) {
        state.selectedCategory?.let { category ->
            state.services.filter { it.category == category }
        } ?: state.services
    }

    val defaultCenter = myLocation ?: LatLng(19.0522, -70.1498)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultCenter, 13f)
    }

    LaunchedEffect(defaultCenter) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(defaultCenter, 14f)
        )
    }

    val mapProperties = MapProperties(
        isMyLocationEnabled = hasLocationPermission
    )

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
            cameraPositionState = cameraPositionState,
            properties = mapProperties
        ) {
            services.forEach { service ->
                Marker(
                    state = MarkerState(
                        position = LatLng(service.latitude, service.longitude)
                    ),
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
        text = "Puntos de interés (${services.size})",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    if (services.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay servicios cercanos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Intenta cambiar el filtro o buscar en otra ubicación",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(services, key = { it.id }) { service ->
                ServiceItemCard(
                    service = service,
                    onClick = {
                        val gmmIntentUri = Uri.parse(
                            "google.navigation:q=${service.latitude},${service.longitude}&mode=d"
                        )
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }

                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserUri = Uri.parse(
                                "https://www.google.com/maps/dir/?api=1&destination=${service.latitude},${service.longitude}"
                            )
                            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                        }
                    }
                )
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
        ServiceCategory.EMERGENCIA,
        ServiceCategory.GASOLINERA
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
                ServiceCategory.GASOLINERA -> "Gasolinera"
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
    service: ServiceItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
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
