package com.iskorsukov.aniwatcher.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iskorsukov.aniwatcher.ui.Screen
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val items = listOf(
        Screen.MediaScreen,
        Screen.AiringScreen,
        Screen.FollowingScreen
    )
    BottomNavigation(backgroundColor = LocalColors.current.primary) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconDrawableId),
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.labelStringId)
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
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
                },
                selectedContentColor = LocalColors.current.secondary,
                unselectedContentColor = LocalColors.current.onPrimary
            )
        }
    }
}

@Composable
@Preview
private fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "airing",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("media") { }
            composable("airing") { }
            composable("following") { }
        }
    }
}