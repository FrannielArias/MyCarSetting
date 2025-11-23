package edu.ucne.loginapi.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edu.ucne.loginapi.ui.theme.LoginApiTheme

@Composable
fun MyCarSettingApp() {
    LoginApiTheme {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                MyCarSettingBottomBar(
                    navController = navController
                )
            }
        ) { padding ->
            MyCarSettingNavHost(
                navController = navController,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
