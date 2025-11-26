package edu.ucne.loginapi.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class AppDestination(val route: String) {
    object Splash : AppDestination("splash")
    object Login : AppDestination("login")
    object Dashboard : AppDestination("dashboard")
    object Maintenance : AppDestination("maintenance")
    object UserCar : AppDestination("user_car")
    object History : AppDestination("history")
    object Manual : AppDestination("manual")
    object Chat : AppDestination("chat")
    object Services : AppDestination("services")
}

@Composable
fun MyCarSettingNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.route,
        modifier = modifier
    ) {
        composable(AppDestination.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(AppDestination.Login.route) {
            UsuariosScreen(navController = navController)
        }
        composable(AppDestination.Dashboard.route) {
            DashboardScreen(
                onNavigateToMaintenance = {
                    navController.navigate(AppDestination.Maintenance.route)
                }
            )
        }
        composable(AppDestination.Maintenance.route) {
            MaintenanceScreen()
        }
        composable(AppDestination.UserCar.route) {
            UserCarScreen()
        }
        composable(AppDestination.History.route) {
            MaintenanceHistoryScreen()
        }
        composable(AppDestination.Manual.route) {
            ManualScreen()
        }
        composable(AppDestination.Chat.route) {
            ChatScreen()
        }
        composable(AppDestination.Services.route) {
            ServicesScreen()
        }
    }
}