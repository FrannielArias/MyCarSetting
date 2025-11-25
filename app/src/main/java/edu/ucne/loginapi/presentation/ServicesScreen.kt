@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServicesScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servicios cercanos") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Próximamente: mapa con talleres, gasolineras y puntos de interés.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Talleres mecánicos")
                        Text("Verás talleres cercanos según tu ubicación.")
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Gasolineras")
                        Text("Verás estaciones de combustible cercanas.")
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Estaciones de carga eléctrica")
                        Text("Soporte para vehículos eléctricos.")
                    }
                }
            }
        }
    }
}
