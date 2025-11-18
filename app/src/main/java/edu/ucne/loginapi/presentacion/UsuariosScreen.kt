package edu.ucne.loginapi.presentacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import edu.ucne.loginapi.domain.model.Usuarios

@Composable
fun UsuariosScreen(
    viewModel: UsuariosViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    UsuariosScreenBody(
        state = state,
        onEvent = viewModel::onEvent,
        onLoginSuccess = onLoginSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreenBody(
    state: UsuarioUiState,
    onEvent: (UsuariosUiEvent) -> Unit,
    onLoginSuccess: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
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
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Email or Username Field
                    OutlinedTextField(
                        value = state.userName,
                        onValueChange = { onEvent(UsuariosUiEvent.UserNameChange(it)) },
                        label = { Text("Email or username") },
                        placeholder = { Text("Email or username") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        isError = state.error != null
                    )

                    // Password Field
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onEvent(UsuariosUiEvent.PasswordChange(it)) },
                        label = { Text("Password") },
                        placeholder = { Text("Password") },
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        isError = state.error != null
                    )

                    // Error Message
                    if (state.error != null) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Success Message
                    if (state.message != null) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Login Button
                    Button(
                        onClick = {
                            if (state.userName.isNotBlank() && state.password.isNotBlank()) {
                                // Aquí puedes agregar lógica de login
                                onLoginSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(bottom = 24.dp),
                        enabled = state.userName.isNotBlank() && state.password.isNotBlank(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Text(
                            text = "Log in",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Register Link
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
                            onClick = { onEvent(UsuariosUiEvent.ShowBottonSheet) }
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

            // Bottom Sheet para Registro
            if (state.isSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        onEvent(UsuariosUiEvent.HideBottonSheet)
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
                            text = if (state.usuariosIs > 0) "Editar Usuario" else "Nuevo Usuario",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.userName,
                            onValueChange = { onEvent(UsuariosUiEvent.UserNameChange(it)) },
                            label = { Text("Nombre de Usuario") },
                            placeholder = { Text("Ingrese su nombre de usuario") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { onEvent(UsuariosUiEvent.PasswordChange(it)) },
                            label = { Text("Contraseña") },
                            placeholder = { Text("Ingrese su contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onEvent(UsuariosUiEvent.HideBottonSheet)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    if (state.userName.isNotBlank() && state.password.isNotBlank()) {
                                        val usuario = Usuarios(
                                            usuarioId = if (state.usuariosIs > 0) state.usuariosIs else null,
                                            userName = state.userName,
                                            password = state.password
                                        )
                                        onEvent(UsuariosUiEvent.Crear(usuario))
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
        isSheetVisible = false
    )
    MaterialTheme {
        UsuariosScreenBody(
            state = sampleState,
            onEvent = {}
        )
    }
}