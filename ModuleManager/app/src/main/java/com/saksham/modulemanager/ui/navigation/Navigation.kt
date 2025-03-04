package com.saksham.modulemanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.saksham.modulemanager.ui.ModuleDetailScreen
import com.saksham.modulemanager.ui.ModuleInstallScreen
import com.saksham.modulemanager.ui.RepositoryDetailScreen
import com.saksham.modulemanager.ui.screens.home.HomeScreen
import com.saksham.modulemanager.ui.screens.repositories.RepositoriesScreen
import com.saksham.modulemanager.ui.screens.settings.SettingsScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val HOME = "home"
    const val REPOSITORIES = "repositories"
    const val SETTINGS = "settings"
    const val MODULE_DETAIL = "module_detail/{moduleId}"
    const val REPOSITORY_DETAIL = "repository_detail/{repositoryId}"
    const val MODULE_INSTALL = "module_install/{moduleId}"
}

/**
 * Main navigation component for the app
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        
        composable(Routes.REPOSITORIES) {
            RepositoriesScreen(navController)
        }
        
        composable(Routes.SETTINGS) {
            SettingsScreen(navController)
        }
        
        composable(
            route = Routes.MODULE_DETAIL,
            arguments = listOf(
                navArgument("moduleId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            ModuleDetailScreen(navController, moduleId)
        }
        
        composable(
            route = Routes.REPOSITORY_DETAIL,
            arguments = listOf(
                navArgument("repositoryId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val repositoryId = backStackEntry.arguments?.getString("repositoryId") ?: ""
            RepositoryDetailScreen(navController, repositoryId)
        }
        
        composable(
            route = Routes.MODULE_INSTALL,
            arguments = listOf(
                navArgument("moduleId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            ModuleInstallScreen(navController, moduleId)
        }
    }
}
