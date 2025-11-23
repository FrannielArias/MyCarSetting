package edu.ucne.loginapi.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController


@Composable
fun MyCarSettingApp() {
    MaterialTheme {
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
