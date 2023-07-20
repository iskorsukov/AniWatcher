package com.iskorsukov.aniwatcher.ui.main.state

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.Screen
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem

@Composable
fun rememberMainScreenState(
    settingsRepository: SettingsRepository,
    mainScreenData: MainScreenData,
    notificationsRationaleDialogState: NotificationsRationaleDialogState,
    navController: NavHostController = rememberNavController(),
    searchFieldState: SearchFieldState = rememberSearchFieldState(),
    seasonYearDialogState: SeasonYearDialogState = rememberSeasonYearDialogState(
        settingsRepository = settingsRepository
    )
): MainScreenState {
    return remember(
        settingsRepository,
        mainScreenData,
        navController,
        searchFieldState,
        seasonYearDialogState,
        notificationsRationaleDialogState
    ) {
        MainScreenState(
            settingsRepository = settingsRepository,
            mainScreenData = mainScreenData,
            navController = navController,
            searchFieldState = searchFieldState,
            seasonYearDialogState = seasonYearDialogState,
            notificationsRationaleDialogState = notificationsRationaleDialogState
        )
    }
}

class MainScreenState(
    settingsRepository: SettingsRepository,
    val mainScreenData: MainScreenData,
    val navController: NavHostController,
    val searchFieldState: SearchFieldState,
    val seasonYearDialogState: SeasonYearDialogState,
    val notificationsRationaleDialogState: NotificationsRationaleDialogState
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val screen: Screen?
        @Composable get() = Screen.ofRoute(
            currentDestination?.route.orEmpty()
        )

    val settingsState = settingsRepository.settingsStateFlow

    val shouldShowErrorDialog = mainScreenData.errorItem != null

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

    fun <T: ComponentActivity> navigateToActivity(
        activityClass: Class<T>,
        extrasBundle: Bundle = Bundle.EMPTY
    ) {
        navController.context.startActivity(
            Intent(navController.context, activityClass).apply {
                putExtras(extrasBundle)
            }
        )
    }
}

data class MainScreenData(
    val isRefreshing: Boolean = false,
    val errorItem: ErrorItem? = null,
    val unreadNotificationsCount: Int = 0
)