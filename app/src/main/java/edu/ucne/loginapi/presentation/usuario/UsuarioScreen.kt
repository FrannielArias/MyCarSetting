package edu.ucne.loginapi.presentation.usuario

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import edu.ucne.franniel_arias_ap2_p2.R
import edu.ucne.loginapi.domain.model.Usuarios
import edu.ucne.loginapi.presentation.AppDestination
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    navController: NavHostController,
    viewModel: UsuarioViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            navController.navigate(AppDestination.Dashboard.route) {
                popUpTo(0) { inclusive = true }
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
fun LoginLogoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.mycar_logo),
            contentDescription = "MyCarSetting logo",
            modifier = Modifier.height(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "MyCarSetting",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreenBody(
    state: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    MyCarLoadingIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LoginContent(state = state, onEvent = onEvent)
                }
            }

            if (state.isSheetVisible) {
                RegisterBottomSheet(
                    state = state,
                    sheetState = sheetState,
                    onEvent = onEvent
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginContent(
    state: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            LoginLogoSection()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    LoginForm(
                        userName = state.userName,
                        password = state.password,
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityChange = { passwordVisible = it },
                        hasError = state.error != null,
                        onEvent = onEvent
                    )

                    MessageSection(
                        error = state.error,
                        message = state.message
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LoginButton(
                        enabled = state.userName.isNotBlank() && state.password.isNotBlank(),
                        onClick = { onEvent(UsuarioEvent.Login) }
                    )

                    RegisterPrompt(onShowSheet = { onEvent(UsuarioEvent.ShowBottonSheet) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginForm(
    userName: String,
    password: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    hasError: Boolean,
    onEvent: (UsuarioEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = userName,
            onValueChange = { onEvent(UsuarioEvent.UserNameChange(it)) },
            label = { Text("Nombre de usuario") },
            placeholder = { Text("Ingrese su usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = hasError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { onEvent(UsuarioEvent.PasswordChange(it)) },
            label = { Text("Contraseña") },
            placeholder = { Text("Ingrese su contraseña") },
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                PasswordVisibilityToggle(
                    visible = passwordVisible,
                    onToggle = onPasswordVisibilityChange
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = hasError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordVisibilityToggle(
    visible: Boolean,
    onToggle: (Boolean) -> Unit
) {
    TextButton(onClick = { onToggle(!visible) }) {
        Text(
            text = if (visible) "Ocultar" else "Mostrar",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageSection(
    error: String?,
    message: String?
) {
    if (error != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (message != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LoginButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RegisterPrompt(onShowSheet: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "¿No tienes usuario?",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(4.dp))
        TextButton(onClick = onShowSheet) {
            Text(
                text = "Créalo aquí",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterBottomSheet(
    state: UsuarioUiState,
    sheetState: SheetState,
    onEvent: (UsuarioEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(UsuarioEvent.HideBottonSheet) },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nuevo usuario",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            RegisterForm(state = state, onEvent = onEvent)

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            RegisterButtons(
                enabled = state.userName.isNotBlank() && state.password.isNotBlank(),
                onCancel = { onEvent(UsuarioEvent.HideBottonSheet) },
                onSave = {
                    if (state.userName.isNotBlank() && state.password.isNotBlank()) {
                        val usuario = Usuarios(
                            usuarioId = null,
                            userName = state.userName,
                            password = state.password
                        )
                        onEvent(UsuarioEvent.Crear(usuario))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(
    state: UsuarioUiState,
    onEvent: (UsuarioEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = state.userName,
            onValueChange = { onEvent(UsuarioEvent.UserNameChange(it)) },
            label = { Text("Nombre de usuario") },
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterButtons(
    enabled: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancelar")
        }
        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = enabled
        ) {
            Text("Guardar")
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
