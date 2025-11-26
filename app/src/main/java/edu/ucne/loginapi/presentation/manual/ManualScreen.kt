@file:OptIn(ExperimentalMaterial3Api::class)

package edu.ucne.loginapi.presentation.manual

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import edu.ucne.loginapi.presentation.manual.ManualViewModel

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
                title = { Text("Manual y ayuda") }
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
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(selectedTabIndex = state.selectedTabIndex) {
                        Tab(
                            selected = state.selectedTabIndex == 0,
                            onClick = { onEvent(ManualEvent.SelectTab(0)) },
                            text = { Text("Testigos tablero") }
                        )
                        Tab(
                            selected = state.selectedTabIndex == 1,
                            onClick = { onEvent(ManualEvent.SelectTab(1)) },
                            text = { Text("Guías y tutoriales") }
                        )
                    }

                    if (state.selectedTabIndex == 0) {
                        WarningLightList(
                            state = state,
                            onEvent = onEvent
                        )
                    } else {
                        GuideArticleList(
                            state = state,
                            onEvent = onEvent
                        )
                    }
                }

                val selectedLight = state.selectedWarningLight
                val selectedArticle = state.selectedArticle

                if (selectedLight != null || selectedArticle != null) {
                    ModalBottomSheet(
                        onDismissRequest = { onEvent(ManualEvent.OnDismissDetail) },
                        sheetState = sheetState
                    ) {
                        if (selectedLight != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = selectedLight.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = selectedLight.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Acción recomendada:",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = selectedLight.action,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (selectedArticle != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = selectedArticle.title,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = selectedArticle.summary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = selectedArticle.content,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WarningLightList(
    state: ManualUiState,
    onEvent: (ManualEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.warningLights, key = { it.id }) { light ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(ManualEvent.OnWarningLightClicked(light.id))
                    }
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
                            text = light.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = light.description,
                            style = MaterialTheme.typography.bodySmall
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.guideArticles, key = { it.id }) { article ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onEvent(ManualEvent.OnGuideClicked(article.id))
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = article.summary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
