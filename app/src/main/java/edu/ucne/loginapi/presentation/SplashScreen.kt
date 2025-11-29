package edu.ucne.loginapi.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import edu.ucne.franniel_arias_ap2_p2.R
import edu.ucne.loginapi.ui.components.MyCarLoadingIndicator

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(state.isCheckingSession) {
        if (!state.isCheckingSession) {
            val destination = if (state.isLoggedIn) {
                AppDestination.Dashboard.route
            } else {
                AppDestination.Login.route
            }

            navController.navigate(destination) {
                popUpTo(AppDestination.Splash.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.mycar_logo),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MyCarSetting",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Organiza el mantenimiento de tu vehículo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            MyCarLoadingIndicator()
        }
    }
}
