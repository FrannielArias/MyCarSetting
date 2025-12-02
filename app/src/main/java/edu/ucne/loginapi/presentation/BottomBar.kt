package edu.ucne.loginapi.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val destination: AppDestination,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(
        destination = AppDestination.Dashboard,
        label = "Inicio",
        icon = Icons.Filled.Dashboard
    ),
    BottomNavItem(
        destination = AppDestination.Maintenance,
        label = "Mant.",
        icon = Icons.Filled.CarRepair
    ),
    BottomNavItem(
        destination = AppDestination.Manual,
        label = "Manual",
        icon = Icons.Filled.Help
    ),
    BottomNavItem(
        destination = AppDestination.Chat,
        label = "Asistente",
        icon = Icons.Filled.Chat
    ),
    BottomNavItem(
        destination = AppDestination.Services,
        label = "Servicios",
        icon = Icons.Filled.Place
    )
)

@Composable
fun MyCarSettingBottomBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentDestination.isDestinationInHierarchy(item.destination.route)
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.destination.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                }
            )
        }
    }
}

private fun NavDestination?.isDestinationInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
