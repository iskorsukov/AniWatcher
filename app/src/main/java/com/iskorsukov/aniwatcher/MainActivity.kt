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
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
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
import com.iskorsukov.aniwatcher.ui.onboarding.OnboardingDialog
import com.iskorsukov.aniwatcher.ui.season.SelectSeasonYearDialog
import com.iskorsukov.aniwatcher.ui.settings.SettingsCompatActivity
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

            val uiState by mainActivityViewModel.uiState
                .collectAsStateWithLifecycle()
            val settingsState by mainActivityViewModel.settingsState
                .collectAsStateWithLifecycle()
            val unreadNotifications by mainActivityViewModel.unreadNotificationsState
                .collectAsStateWithLifecycle()

            val timeInMinutes by timeInMinutesFlow
                .collectAsStateWithLifecycle(initialValue = 0)

            val shouldShowOnboardingDialog by rememberSaveable(settingsState.onboardingComplete) {
                mutableStateOf(!settingsState.onboardingComplete)
            }
            var shouldShowSeasonYearDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var shouldShowErrorDialog by rememberSaveable(uiState.errorItem) {
                mutableStateOf(uiState.errorItem != null)
            }

            AniWatcherTheme(settingsState.darkModeOption) {
                Scaffold(
                    topBar = {
                        TopBar(
                            uiState = uiState,
                            navController = navController,
                            onSettingsClicked = this::startSettingsActivity,
                            onNotificationsClicked = this::startNotificationsActivity,
                            onSearchTextInput = mainActivityViewModel::onSearchTextInput,
                            onSearchFieldOpenChange = mainActivityViewModel::onSearchFieldOpenChange,
                            onSelectSeasonYearClicked = { shouldShowSeasonYearDialog = true },
                            unreadNotifications = unreadNotifications
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            navController = navController,
                            scheduleType = settingsState.scheduleType,
                            onChangedDestination = {
                                mainActivityViewModel.resetTopBarState()
                                mediaViewModel.resetState()
                                airingViewModel.resetState()
                                followingViewModel.resetState()
                            }
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
                        if (shouldShowOnboardingDialog) {
                            OnboardingDialog(
                                onDarkModeOptionSelected = mainActivityViewModel::onDarkModeOptionSelected,
                                onScheduleTypeSelected = mainActivityViewModel::onScheduleTypeSelected,
                                onNamingSchemeSelected = mainActivityViewModel::onPreferredNamingSchemeSelected
                            ) {
                                mainActivityViewModel.onOnboardingComplete()
                            }
                        }
                        if (shouldShowSeasonYearDialog) {
                            SelectSeasonYearDialog(
                                onSeasonYearSelected = mainActivityViewModel::onSeasonYearSelected,
                                onDismissRequest = { shouldShowSeasonYearDialog = false },
                                selectedSeasonYear = uiState.seasonYear
                            )
                        }

                        NavHost(
                            navController = navController,
                            startDestination = if (settingsState.scheduleType == ScheduleType.ALL) "airing" else "airing_season"
                        ) {
                            composable(if (settingsState.scheduleType == ScheduleType.ALL) "media" else "media_season") {
                                MediaScreen(
                                    viewModel = mediaViewModel,
                                    uiState = uiState,
                                    settingsState = settingsState,
                                    timeInMinutes = timeInMinutes,
                                    onMediaClicked = { startDetailsActivity(it.id) },
                                    onRefresh = mainActivityViewModel::loadAiringData,
                                    onGenreChipClicked = {
                                        mainActivityViewModel.onSearchFieldOpenChange(true)
                                        mainActivityViewModel.appendSearchText(it)
                                    }
                                )
                            }
                            composable(if (settingsState.scheduleType == ScheduleType.ALL) "airing" else "airing_season") {
                                AiringScreen(
                                    viewModel = airingViewModel,
                                    uiState = uiState,
                                    settingsState = settingsState,
                                    timeInMinutes = timeInMinutes,
                                    onMediaClicked = { startDetailsActivity(it.id) },
                                    onRefresh = mainActivityViewModel::loadAiringData
                                )
                            }
                            composable("following") {
                                FollowingScreen(
                                    viewModel = followingViewModel,
                                    uiState = uiState,
                                    settingsState = settingsState,
                                    timeInMinutes = timeInMinutes,
                                    onMediaClicked = { startDetailsActivity(it.id) },
                                    onGenreChipClicked = {
                                        mainActivityViewModel.onSearchFieldOpenChange(true)
                                        mainActivityViewModel.appendSearchText(it)
                                    }
                                )
                            }
                        }

                        if (settingsState.onboardingComplete) {
                            LaunchedEffect(settingsState.scheduleType, uiState.seasonYear) {
                                mainActivityViewModel.loadAiringData()
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
                                            ErrorItem.Action.REFRESH -> {
                                                mainActivityViewModel.loadAiringData()
                                            }
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
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
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


