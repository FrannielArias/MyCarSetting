package edu.ucne.loginapi.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.ucne.loginapi.presentation.Services.ServicesScreen
import edu.ucne.loginapi.presentation.chatBot.ChatScreen
import edu.ucne.loginapi.presentation.dashboard.DashboardScreen
import edu.ucne.loginapi.presentation.maintenance.MaintenanceScreen
import edu.ucne.loginapi.presentation.maintenanceHistory.MaintenanceHistoryScreen
import edu.ucne.loginapi.presentation.manual.ManualScreen
import edu.ucne.loginapi.presentation.userCar.UserCarScreen
import edu.ucne.loginapi.presentation.usuario.ProfileScreen
import edu.ucne.loginapi.presentation.usuario.RegisterScreen
import edu.ucne.loginapi.presentation.usuario.UsuariosScreen

sealed class AppDestination(val route: String) {
    object Splash : AppDestination("splash")
    object Login : AppDestination("login")
    object Register : AppDestination("register")
    object Dashboard : AppDestination("dashboard")

    object Maintenance : AppDestination("maintenance") {
        fun createRoute(taskId: String? = null): String {
            return if (taskId.isNullOrBlank()) {
                route
            } else {
                "$route?taskId=$taskId"
            }
        }
    }

    object UserCar : AppDestination("user_car")
    object History : AppDestination("history")
    object Manual : AppDestination("manual")
    object Chat : AppDestination("chat/{conversationId}") {
        fun createRoute(conversationId: String) = "chat/$conversationId"
    }
    object Services : AppDestination("services")
    object Profile : AppDestination("profile")
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
        composable(AppDestination.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(AppDestination.Dashboard.route) {
            DashboardScreen(
                onNavigateToMaintenance = { taskId ->
                    navController.navigate(AppDestination.Maintenance.createRoute(taskId))
                },
                onNavigateToHistory = {
                    navController.navigate(AppDestination.History.route)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestination.Profile.route)
                },
                onNavigateToChat = { conversationId ->
                    navController.navigate(AppDestination.Chat.createRoute(conversationId))
                }
            )
        }
        composable(
            route = AppDestination.Maintenance.route + "?taskId={taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")
            MaintenanceScreen(focusedTaskId = taskId)
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
        composable(
            route = AppDestination.Chat.route,
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val conversationId =
                backStackEntry.arguments?.getString("conversationId") ?: "default_conversation"
            ChatScreen(conversationId = conversationId)
        }
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserCar = {
                    navController.navigate(AppDestination.UserCar.route)
                },
                onLogout = {
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppDestination.Services.route) {
            ServicesScreen()
        }
    }
}