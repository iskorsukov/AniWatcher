package com.iskorsukov.aniwatcher.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.Screen

@Composable
fun rememberMainScreenState(
    settingsRepository: SettingsRepository,
    notificationsPermissionState: NotificationsPermissionState,
    navController: NavHostController = rememberNavController(),
    searchFieldState: SearchFieldState = rememberSearchFieldState(),
    seasonYearDialogState: SeasonYearDialogState = rememberSeasonYearDialogState(
        settingsRepository = settingsRepository
    )
): MainScreenState {
    return remember(
        settingsRepository,
        notificationsPermissionState,
        navController,
        searchFieldState,
        seasonYearDialogState
    ) {
        MainScreenState(
            settingsRepository,
            navController,
            searchFieldState,
            seasonYearDialogState,
            notificationsPermissionState,
        )
    }
}

class MainScreenState(
    settingsRepository: SettingsRepository,
    val navController: NavHostController,
    val searchFieldState: SearchFieldState,
    val seasonYearDialogState: SeasonYearDialogState,
    val notificationsPermissionState: NotificationsPermissionState
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val screen: Screen?
        @Composable get() = Screen.ofRoute(
            currentDestination?.route.orEmpty()
        )

    val settingsState = settingsRepository.settingsStateFlow

    fun navigateToScreen(screen: Screen) {
        navController.navigate(screen.route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}