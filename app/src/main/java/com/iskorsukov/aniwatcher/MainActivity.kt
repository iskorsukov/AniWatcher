package com.iskorsukov.aniwatcher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.iskorsukov.aniwatcher.service.NotificationService
import com.iskorsukov.aniwatcher.ui.airing.AiringScreen
import com.iskorsukov.aniwatcher.ui.airing.AiringViewModel
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import com.iskorsukov.aniwatcher.ui.base.error.ErrorPopupDialogSurface
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import com.iskorsukov.aniwatcher.ui.following.FollowingScreen
import com.iskorsukov.aniwatcher.ui.following.FollowingViewModel
import com.iskorsukov.aniwatcher.ui.main.BottomNavigationBar
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.main.TopBar
import com.iskorsukov.aniwatcher.ui.media.MediaScreen
import com.iskorsukov.aniwatcher.ui.media.MediaViewModel
import com.iskorsukov.aniwatcher.ui.notification.NotificationActivity
import com.iskorsukov.aniwatcher.ui.settings.SettingsCompatActivity
import com.iskorsukov.aniwatcher.ui.sorting.SelectSortingOptionDialog
import com.iskorsukov.aniwatcher.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLifecycleComposeApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val airingViewModel: AiringViewModel by viewModels()
    private val followingViewModel: FollowingViewModel by viewModels()
    private val mediaViewModel: MediaViewModel by viewModels()

    private val timeInMinutesFlow = flow {
        while (true) {
            val timeInMillis = System.currentTimeMillis()
            emit(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }.distinctUntilChanged()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel.loadAiringData()
        startNotificationService()
        setContent {
            val navController = rememberNavController()

            val scaffoldState = rememberScaffoldState()
            val uiState by mainActivityViewModel.uiState.collectAsStateWithLifecycle()

            val unreadNotifications by mainActivityViewModel.unreadNotificationsState
                .collectAsStateWithLifecycle()

            var shouldShowSortingOptionsDialog by remember {
                mutableStateOf(false)
            }
            var shouldShowErrorDialog by remember(uiState.errorItem) {
                mutableStateOf(uiState.errorItem != null)
            }

            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        TopBar(
                            uiState = uiState,
                            navController = navController,
                            onSelectSortingOptionClicked = {
                                shouldShowSortingOptionsDialog = true
                            },
                            onSettingsClicked = { startSettingsActivity() },
                            onNotificationsClicked = { startNotificationsActivity() },
                            onSearchTextInput = mainActivityViewModel::onSearchTextInput,
                            onSearchFieldOpenChange = mainActivityViewModel::onSearchFieldOpenChange,
                            unreadNotifications = unreadNotifications
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    },
                    scaffoldState = scaffoldState,
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        if (shouldShowSortingOptionsDialog) {
                            SelectSortingOptionDialog(
                                onSortingOptionSelected = mainActivityViewModel::onSortingOptionSelected,
                                onDismissRequest = { shouldShowSortingOptionsDialog = false },
                                selectedOption = uiState.sortingOption
                            )
                        }

                        NavHost(
                            navController = navController,
                            startDestination = "airing"
                        ) {
                            composable("media") {
                                MediaScreen(
                                    mainActivityViewModel,
                                    mediaViewModel,
                                    timeInMinutesFlow
                                ) { startDetailsActivity(it.id) }
                            }
                            composable("airing") {
                                AiringScreen(
                                    mainActivityViewModel,
                                    airingViewModel,
                                    timeInMinutesFlow
                                ) { startDetailsActivity(it.id) }
                            }
                            composable("following") {
                                FollowingScreen(
                                    mainActivityViewModel,
                                    followingViewModel,
                                    timeInMinutesFlow
                                ) { startDetailsActivity(it.id) }
                            }
                        }

                        AnimatedVisibility(
                            visible = shouldShowErrorDialog && uiState.errorItem != null,
                            enter = slideInVertically(initialOffsetY = { it * 2 }),
                            exit = slideOutVertically(targetOffsetY = { it * 2 }),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(8.dp)
                        ) {
                            val errorItem = uiState.errorItem!!
                            ErrorPopupDialogSurface(
                                errorItem = errorItem,
                                onActionClicked = {
                                    when (errorItem.action) {
                                        ErrorItem.Action.REFRESH -> mainActivityViewModel.loadAiringData()
                                        ErrorItem.Action.DISMISS -> {}
                                    }
                                },
                                onDismissRequest = { shouldShowErrorDialog = false }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startNotificationService() {
        startService(Intent(this, NotificationService::class.java))
    }

    private fun startDetailsActivity(mediaItemId: Int) {
        startActivity(
            Intent(this, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.MEDIA_ITEM_ID_EXTRA, mediaItemId)
            }
        )
    }

    private fun startSettingsActivity() {
        startActivity(
            Intent(this, SettingsCompatActivity::class.java)
        )
    }

    private fun startNotificationsActivity() {
        startActivity(
            Intent(this, NotificationActivity::class.java)
        )
    }
}


