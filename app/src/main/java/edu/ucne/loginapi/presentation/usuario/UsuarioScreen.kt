package edu.ucne.loginapi.presentation.usuario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.presentation.AppDestination
import edu.ucne.loginapi.presentation.usuario.UsuarioUiState
import edu.ucne.loginapi.presentation.usuario.UsuarioViewModel

@Composable
fun UsuariosScreen(
    navController: NavHostController,
    viewModel: UsuarioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            navController.navigate(AppDestination.Dashboard.route) {
                popUpTo(AppDestination.Login.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    UsuariosScreenBody(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreenBody(
    state: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            if (state.isLoggedIn) {
                TopAppBar(
                    title = { Text("Mi App") },
                    actions = {
                        IconButton(onClick = { onEvent(UsuarioEvent.Logout) }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar sesión"
                            )
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Iniciar Sesión",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 48.dp)
                        )

                        OutlinedTextField(
                            value = state.userName,
                            onValueChange = { onEvent(UsuarioEvent.UserNameChange(it)) },
                            label = { Text("Nombre de usuario") },
                            placeholder = { Text("Ingrese su usuario") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.error != null
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { onEvent(UsuarioEvent.PasswordChange(it)) },
                            label = { Text("Contraseña") },
                            placeholder = { Text("Ingrese su contraseña") },
                            singleLine = true,
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.error != null
                        )

                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (state.message != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { onEvent(UsuarioEvent.Login) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = state.userName.isNotBlank() && state.password.isNotBlank(),
                            shape = MaterialTheme.shapes.extraLarge
                        ) {
                            Text(
                                text = "Log in",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No tienes Usuario?",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = { onEvent(UsuarioEvent.ShowBottonSheet) }
                            ) {
                                Text(
                                    text = "Crealo aquí",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            }
                        }
                    }
                }
            }

            if (state.isSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        onEvent(UsuarioEvent.HideBottonSheet)
                    },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Nuevo Usuario",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.userName,
                            onValueChange = { onEvent(UsuarioEvent.UserNameChange(it)) },
                            label = { Text("Nombre de Usuario") },
                            placeholder = { Text("Ingrese su nombre de usuario") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { onEvent(UsuarioEvent.PasswordChange(it)) },
                            label = { Text("Contraseña") },
                            placeholder = { Text("Ingrese su contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (state.error != null) {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onEvent(UsuarioEvent.HideBottonSheet)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    if (state.userName.isNotBlank() && state.password.isNotBlank()) {
                                        val usuario = Usuarios(
                                            usuarioId = null,
                                            userName = state.userName,
                                            password = state.password
                                        )
                                        onEvent(UsuarioEvent.Crear(usuario))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = state.userName.isNotBlank() && state.password.isNotBlank()
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UsuariosScreenPreview() {
    val sampleState = UsuarioUiState(
        userName = "",
        password = "",
        isLoading = false,
        isSheetVisible = false,
        isLoggedIn = false
    )
    MaterialTheme {
        UsuariosScreenBody(
            state = sampleState,
            onEvent = {}
        )
    }
}