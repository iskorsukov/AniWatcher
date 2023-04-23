package com.iskorsukov.aniwatcher.ui.main

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.Screen
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun BottomNavigationBar(
    mainScreenState: MainScreenState
) {
    val items = mutableListOf<Screen>()
    items.add(Screen.MediaScreen)
    items.add(Screen.AiringScreen)
    items.add(Screen.FollowingScreen)

    val settingsState by mainScreenState.settingsState.collectAsStateWithLifecycle()
    val isThisWeekSelected = settingsState.selectedSeasonYear == DateTimeHelper.SeasonYear.THIS_WEEK
    BottomNavigation(backgroundColor = LocalColors.current.primary) {
        val currentDestination = mainScreenState.currentDestination
        items.forEach { screenItem ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screenItem.iconDrawableId),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = if (isThisWeekSelected && screenItem.thisWeekLabelStringId != null)
                            stringResource(screenItem.thisWeekLabelStringId)
                        else
                            stringResource(screenItem.labelStringId)
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screenItem.route } == true,
                onClick = {
                    if (screenItem.route != currentDestination?.route) {
                        mainScreenState.searchFieldState.reset()
                    }
                    mainScreenState.navigateToScreen(screenItem)
                },
                selectedContentColor = LocalColors.current.secondary,
                unselectedContentColor = LocalColors.current.onPrimary
            )
        }
    }
}