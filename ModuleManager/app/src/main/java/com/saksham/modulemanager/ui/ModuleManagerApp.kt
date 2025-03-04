package com.saksham.modulemanager.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.saksham.modulemanager.ui.components.BottomNavigationBar
import com.saksham.modulemanager.ui.navigation.AppNavigation
import com.saksham.modulemanager.ui.navigation.Routes

/**
 * Main app composable that sets up the navigation and scaffold
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleManagerApp(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.HOME
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Screen routes
 */
sealed class Screen(val route: String, val title: String) {
    object Home : Screen(Routes.HOME, "Home")
    object Repositories : Screen(Routes.REPOSITORIES, "Repositories")
    object Settings : Screen(Routes.SETTINGS, "Settings")
    object ModuleDetail : Screen("module_detail", "Module Details")
    object RepositoryDetail : Screen("repository_detail", "Repository Details")
    object ModuleInstall : Screen("module_install", "Install Module")
}
