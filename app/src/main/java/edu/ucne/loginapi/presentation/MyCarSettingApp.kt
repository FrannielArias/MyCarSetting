package edu.ucne.loginapi.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.ucne.loginapi.ui.theme.LoginApiTheme

@Composable
fun MyCarSettingApp() {
    LoginApiTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute != AppDestination.Login.route

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    MyCarSettingBottomBar(
                        navController = navController
                    )
                }
            }
        ) { padding ->
            MyCarSettingNavHost(
                navController = navController,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
