package edu.ucne.loginapi.presentation.usuario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import edu.ucne.loginapi.presentation.Register.RegisterEvent
import edu.ucne.loginapi.presentation.Register.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message, state.error) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(RegisterEvent.MessageShown)
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(RegisterEvent.MessageShown)
        }
    }

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            navController.popBackStack()
            viewModel.onEvent(RegisterEvent.NavigationHandled)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Regístrate en MyCarSetting",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.userName,
                onValueChange = { viewModel.onEvent(RegisterEvent.UserNameChange(it)) },
                label = { Text("Nombre de usuario") },
                placeholder = { Text("Ingrese su nombre de usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChange(it)) },
                label = { Text("Contraseña") },
                placeholder = { Text("Ingrese su contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChange(it)) },
                label = { Text("Confirmar contraseña") },
                placeholder = { Text("Repita su contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onEvent(RegisterEvent.Submit) },
                enabled = !state.isLoading &&
                        state.userName.isNotBlank() &&
                        state.password.isNotBlank() &&
                        state.confirmPassword.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp)
                    )
                } else {
                    Text("Crear cuenta")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
