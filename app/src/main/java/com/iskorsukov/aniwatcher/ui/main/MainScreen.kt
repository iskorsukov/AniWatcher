package com.iskorsukov.aniwatcher.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.ui.airing.AiringScreen
import com.iskorsukov.aniwatcher.ui.airing.AiringViewModel
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorPopupDialogSurface
import com.iskorsukov.aniwatcher.ui.following.FollowingScreen
import com.iskorsukov.aniwatcher.ui.following.FollowingViewModel
import com.iskorsukov.aniwatcher.ui.media.MediaScreen
import com.iskorsukov.aniwatcher.ui.media.MediaViewModel
import com.iskorsukov.aniwatcher.ui.permission.NotificationsPermissionRationaleDialog
import com.iskorsukov.aniwatcher.ui.season.SelectSeasonYearDialog
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun MainScreen(
    uiState: MainActivityUiState,
    mainScreenState: MainScreenState,
    settingsState: SettingsState,
    onRefresh: () -> Unit,
    onStartSettings: () -> Unit,
    onStartNotifications: () -> Unit,
    onStartDetails: (MediaItem) -> Unit,
    mediaItemMapper: MediaItemMapper,
    mediaViewModel: MediaViewModel = viewModel(),
    airingViewModel: AiringViewModel = viewModel(),
    followingViewModel: FollowingViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()

    var shouldShowErrorDialog by rememberSaveable(uiState.errorItem) {
        mutableStateOf(uiState.errorItem != null)
    }
    val shouldShowNotificationsRationaleDialog by mainScreenState
        .notificationsPermissionState
        .showNotificationsRationaleDialog
        .collectAsStateWithLifecycle()
    val shouldShowSeasonYearDialog = mainScreenState
        .seasonYearDialogState
        .showSelectSeasonYearDialog

    AniWatcherTheme(settingsState.darkModeOption) {
        Scaffold(
            topBar = {
                TopBar(
                    mainScreenState = mainScreenState,
                    mainActivityUiState = uiState,
                    onSettingsClicked = onStartSettings,
                    onNotificationsClicked = onStartNotifications,
                    onSelectSeasonYearClicked = {
                        mainScreenState.seasonYearDialogState.showSelectSeasonYearDialog = true
                    }
                )
            },
            bottomBar = {
                BottomNavigationBar(
                    mainScreenState = mainScreenState
                )
            },
            scaffoldState = scaffoldState,
            backgroundColor = LocalColors.current.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (shouldShowSeasonYearDialog) {
                    SelectSeasonYearDialog(
                        mainScreenState.seasonYearDialogState
                    )
                }
                if (shouldShowNotificationsRationaleDialog) {
                    NotificationsPermissionRationaleDialog(
                        mainScreenState.notificationsPermissionState
                    )
                }

                NavHost(
                    navController = mainScreenState.navController,
                    startDestination = "airing"
                ) {
                    composable("media") {
                        MediaScreen(
                            viewModel = mediaViewModel,
                            isRefreshing = uiState.isRefreshing,
                            onMediaClicked = onStartDetails,
                            onRefresh = onRefresh,
                            mediaItemMapper = mediaItemMapper,
                            searchFieldState = mainScreenState.searchFieldState,
                            preferredNamingScheme = settingsState.preferredNamingScheme
                        )
                    }
                    composable("airing") {
                        AiringScreen(
                            viewModel = airingViewModel,
                            mediaItemMapper = mediaItemMapper,
                            isRefreshing = uiState.isRefreshing,
                            settingsState = settingsState,
                            onMediaClicked = onStartDetails,
                            onRefresh = onRefresh
                        )
                    }
                    composable("following") {
                        FollowingScreen(
                            viewModel = followingViewModel,
                            mediaItemMapper = mediaItemMapper,
                            settingsState = settingsState,
                            onMediaClicked = onStartDetails,
                            searchFieldState = mainScreenState.searchFieldState
                        )
                    }
                }

                AnimatedErrorDialogSurface(
                    isVisible = shouldShowErrorDialog,
                    errorItem = uiState.errorItem,
                    onRefresh = onRefresh,
                    onDismiss = { shouldShowErrorDialog = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun AnimatedErrorDialogSurface(
    isVisible: Boolean,
    errorItem: ErrorItem?,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it * 2 }),
        exit = slideOutVertically(targetOffsetY = { it * 2 }),
        modifier = modifier
    ) {
        if (errorItem != null) {
            ErrorPopupDialogSurface(
                errorItem = errorItem,
                onActionClicked = {
                    when (errorItem.action) {
                        ErrorItem.Action.REFRESH -> {
                            onRefresh.invoke()
                        }
                        ErrorItem.Action.DISMISS -> {}
                    }
                },
                onDismissRequest = onDismiss,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}