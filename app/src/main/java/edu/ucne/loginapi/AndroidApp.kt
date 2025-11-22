package edu.ucne.loginapi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edu.ucne.loginapi.presentation.MyCarSettingBottomBar
import edu.ucne.loginapi.presentation.MyCarSettingNavHost
import edu.ucne.loginapi.ui.theme.LoginApiTheme

@Composable
fun AndroidApp() {
    LoginApiTheme {
        val navController = rememberNavController()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    MyCarSettingBottomBar(navController = navController)
                }
            ) { innerPadding ->
                MyCarSettingNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
