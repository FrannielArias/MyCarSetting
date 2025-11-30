@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.manual

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import edu.ucne.loginapi.domain.model.GuideArticle
import edu.ucne.loginapi.domain.model.WarningLight
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator

@Composable
fun ManualScreen(
    viewModel: ManualViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ManualBody(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ManualBody(
    state: ManualUiState,
    onEvent: (ManualEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(state.userMessage) {
        state.userMessage?.let {
            snackbarHostState.showSnackbar(it)
            onEvent(ManualEvent.OnUserMessageShown)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manual y ayuda",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                MyCarLoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ManualContent(state = state, onEvent = onEvent)

                ManualDetailSheet(
                    state = state,
                    sheetState = sheetState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@Composable
private fun ManualContent(
    state: ManualUiState,
    onEvent: (ManualEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ManualTabRow(
            selectedTabIndex = state.selectedTabIndex,
            onEvent = onEvent
        )

        if (state.selectedTabIndex == 0) {
            WarningLightList(state = state, onEvent = onEvent)
        } else {
            GuideArticleList(state = state, onEvent = onEvent)
        }
    }
}

@Composable
private fun ManualTabRow(
    selectedTabIndex: Int,
    onEvent: (ManualEvent) -> Unit
) {
    TabRow(selectedTabIndex = selectedTabIndex) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onEvent(ManualEvent.SelectTab(0)) },
            text = { Text("Testigos tablero") }
        )
        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onEvent(ManualEvent.SelectTab(1)) },
            text = { Text("Guías y tutoriales") }
        )
    }
}

@Composable
private fun ManualDetailSheet(
    state: ManualUiState,
    sheetState: SheetState,
    onEvent: (ManualEvent) -> Unit
) {
    val selectedLight = state.selectedWarningLight
    val selectedArticle = state.selectedArticle

    if (selectedLight != null || selectedArticle != null) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ManualEvent.OnDismissDetail) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    selectedLight != null -> WarningLightDetail(light = selectedLight)
                    selectedArticle != null -> GuideArticleDetail(article = selectedArticle)
                }
            }
        }
    }
}

@Composable
private fun WarningLightDetail(light: WarningLight) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = light.name,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = light.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Acción recomendada",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = light.action,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun GuideArticleDetail(article: GuideArticle) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = article.title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = article.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = article.content,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun WarningLightList(
    state: ManualUiState,
    onEvent: (ManualEvent) -> Unit
) {
    if (state.warningLights.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay testigos de tablero registrados.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.warningLights, key = { it.id }) { light ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // ✅ light.id es Int, coincide con OnWarningLightClicked(id: Int)
                        onEvent(ManualEvent.OnWarningLightClicked(light.id))
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = light.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = light.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuideArticleList(
    state: ManualUiState,
    onEvent: (ManualEvent) -> Unit
) {
    if (state.guideArticles.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay guías disponibles por el momento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.guideArticles, key = { it.id }) { article ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // ✅ article.id es String, coincide con OnGuideClicked(id: String)
                        onEvent(ManualEvent.OnGuideClicked(article.id))
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}