package com.iskorsukov.aniwatcher

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.*
import com.iskorsukov.aniwatcher.domain.notification.alarm.NotificationsAlarmBuilder
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
import kotlinx.coroutines.launch
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

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.settingsState.collect { state ->
                    if (state.notificationsEnabled) {
                        scheduleNotificationChecks()
                    } else {
                        cancelNotificationChecks()
                    }
                }
            }
        }

        setContent {
            val navController = rememberNavController()

            val scaffoldState = rememberScaffoldState()
            val uiState by mainActivityViewModel.uiState.collectAsStateWithLifecycle()

            val unreadNotifications by mainActivityViewModel.unreadNotificationsState
                .collectAsStateWithLifecycle()

            var shouldShowSortingOptionsDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var shouldShowErrorDialog by rememberSaveable(uiState.errorItem) {
                mutableStateOf(uiState.errorItem != null)
            }

            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        TopBar(
                            uiState = uiState,
                            navController = navController,
                            onSelectSortingOptionClicked = { shouldShowSortingOptionsDialog = true },
                            onSettingsClicked = this::startSettingsActivity,
                            onNotificationsClicked = this::startNotificationsActivity,
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
                            visible = shouldShowErrorDialog,
                            enter = slideInVertically(initialOffsetY = { it * 2 }),
                            exit = slideOutVertically(targetOffsetY = { it * 2 }),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) {
                            val errorItem = uiState.errorItem
                            if (errorItem != null) {
                                ErrorPopupDialogSurface(
                                    errorItem = errorItem,
                                    onActionClicked = {
                                        when (errorItem.action) {
                                            ErrorItem.Action.REFRESH -> mainActivityViewModel.loadAiringData()
                                            ErrorItem.Action.DISMISS -> {}
                                        }
                                    },
                                    onDismissRequest = { shouldShowErrorDialog = false },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scheduleNotificationChecks() {
        (getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
            ?.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                NotificationsAlarmBuilder.buildAlarmIntent(this)
            )
    }

    private fun cancelNotificationChecks() {
        val cancellationCheckIntent =
            NotificationsAlarmBuilder.buildCancellationCheckAlarmIntent(this)
        if (cancellationCheckIntent != null) {
            (getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
                ?.cancel(cancellationCheckIntent)
        }
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


