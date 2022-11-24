package com.iskorsukov.aniwatcher.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.Screen
import com.iskorsukov.aniwatcher.ui.base.topbar.SearchField
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TopBar(
    uiState: MainActivityUiState,
    navController: NavHostController,
    onSelectSortingOptionClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onSearchTextInput: (String) -> Unit,
    onSearchFieldOpenChange: (Boolean) -> Unit,
    unreadNotifications: Int = 0
) {
    val focusRequester = remember { FocusRequester() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val screen = Screen.ofRoute(currentDestination?.route.orEmpty())

    TopAppBar(backgroundColor = LocalColors.current.primary) {
        if (screen?.hasSearchBar == true) {
            AnimatedSearchField(
                uiState = uiState,
                onSearchTextInput = onSearchTextInput,
                onSearchFieldOpenChange = onSearchFieldOpenChange,
                focusRequester = focusRequester
            )
        }
        if (screen?.hasSortingOptions == true) {
            IconButton(onClick = onSelectSortingOptionClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_sort_24),
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
            }
        }
        Spacer(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        IconButton(onClick = onNotificationsClicked) {
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
                if (unreadNotifications > 0) {
                    Text(
                        text = unreadNotifications.coerceAtMost(99).toString(),
                        style = LocalTextStyles.current.contentSmallEmphasisWhite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                LocalColors.current.attentionBackground,
                                CircleShape
                            )
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
        IconButton(onClick = onSettingsClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
    }
}

@Composable
private fun AnimatedSearchField(
    uiState: MainActivityUiState,
    onSearchTextInput: (String) -> Unit,
    onSearchFieldOpenChange: (Boolean) -> Unit,
    focusRequester: FocusRequester
) {
    AnimatedVisibility(
        visible = uiState.searchFieldOpen,
        enter = expandHorizontally(expandFrom = Alignment.Start),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
    ) {
        SearchField(
            searchText = uiState.searchText,
            onSearchTextChanged = onSearchTextInput,
            onSearchCancelled = {
                onSearchFieldOpenChange.invoke(false)
            },
            focusRequester = focusRequester
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    if (!uiState.searchFieldOpen) {
        IconButton(
            onClick = {
                onSearchFieldOpenChange.invoke(true)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
    }
}

@Composable
@Preview
private fun AnimatedSearchFieldPreview() {
    val focusRequester = remember { FocusRequester() }
    val uiStateFlow = remember {
        mutableStateOf(MainActivityUiState(false))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LocalColors.current.primary)
    ) {
        if (uiStateFlow.value.searchFieldOpen) {
            AnimatedSearchField(
                uiState = uiStateFlow.value,
                onSearchTextInput = {
                    uiStateFlow.value = MainActivityUiState(
                        false,
                        searchText = it,
                        searchFieldOpen = true
                    )
                },
                onSearchFieldOpenChange = {
                    uiStateFlow.value = MainActivityUiState(false, searchFieldOpen = it)
                },
                focusRequester = focusRequester
            )
        } else {
            IconButton(
                onClick = {
                    uiStateFlow.value = MainActivityUiState(false, searchFieldOpen = true)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
            }
        }
    }
}

@Composable
@Preview
private fun TopBarPreview() {
    val navController = rememberNavController()
    val uiStateFlow = MutableStateFlow(MainActivityUiState(false))
    val uiState by uiStateFlow.collectAsState()
    Scaffold(
        topBar = {
            TopBar(
                uiState = uiState,
                navController = navController,
                onSelectSortingOptionClicked = { },
                onSettingsClicked = { },
                onNotificationsClicked = { },
                onSearchFieldOpenChange = { open ->
                    uiStateFlow.value =
                        MainActivityUiState(
                            false,
                            searchFieldOpen = open
                        )
                },
                onSearchTextInput = { input ->
                    uiStateFlow.value =
                        MainActivityUiState(
                            false,
                            searchFieldOpen = true,
                            searchText = input
                        )
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "media",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("media") { }
        }
    }
}

@Composable
@Preview
private fun TopBarNoSearchAndOptionsPreview() {
    val navController = rememberNavController()
    val uiState = MainActivityUiState(false)
    Scaffold(
        topBar = {
            TopBar(
                uiState = uiState,
                navController = navController,
                onSelectSortingOptionClicked = { },
                onSettingsClicked = { },
                onNotificationsClicked = { },
                onSearchFieldOpenChange = { },
                onSearchTextInput = { }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "airing",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("airing") { }
        }
    }
}

@Composable
@Preview
private fun TopBarUnreadNotificationsPreview() {
    val navController = rememberNavController()
    val uiState = MainActivityUiState(false)
    Scaffold(
        topBar = {
            TopBar(
                uiState = uiState,
                navController = navController,
                onSelectSortingOptionClicked = { },
                onSettingsClicked = { },
                onNotificationsClicked = { },
                onSearchFieldOpenChange = { },
                onSearchTextInput = { },
                unreadNotifications = 17
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "airing",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("airing") { }
        }
    }
}