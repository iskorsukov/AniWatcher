package com.iskorsukov.aniwatcher.ui.main

import android.Manifest
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.domain.notification.alarm.NotificationsAlarmBuilder
import com.iskorsukov.aniwatcher.domain.notification.alarm.NotificationsBootReceiver
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.main.screen.MainScreen
import com.iskorsukov.aniwatcher.ui.main.state.rememberMainScreenState
import com.iskorsukov.aniwatcher.ui.main.state.rememberNotificationsRationaleDialogState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var mediaItemMapper: MediaItemMapper

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private val requestNotificationsPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            settingsRepository.setNotificationsEnabled(isGranted)
            if (!isGranted) {
                Toast.makeText(this, R.string.notifications_disabled, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val mainScreenData by mainActivityViewModel.dataFlow
                .collectAsStateWithLifecycle()

            val settingsState by settingsRepository.settingsStateFlow.collectAsStateWithLifecycle()
            val notificationsRationaleDialogState = rememberNotificationsRationaleDialogState(
                requestLauncher = requestNotificationsPermissionLauncher
            )

            val mainScreenState = rememberMainScreenState(
                settingsRepository = settingsRepository,
                mainScreenData = mainScreenData,
                notificationsRationaleDialogState = notificationsRationaleDialogState
            )

            LaunchedEffect(
                settingsState.selectedSeasonYear
            ) {
                mainActivityViewModel.loadAiringData()
            }

            LaunchedEffect(settingsState.notificationsEnabled) {
                if (settingsState.notificationsEnabled) {
                    enableBootReceiver()
                    scheduleNotificationChecks()
                } else {
                    disableBootReceiver()
                    cancelNotificationChecks()
                }
            }

            MainScreen(
                mainScreenState = mainScreenState,
                onRefresh = mainActivityViewModel::loadAiringData,
                onFollowMedia = { mediaItem ->
                    mainActivityViewModel.onFollowMedia(mediaItem)
                    if (!mediaItem.isFollowing) {
                        when {
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                // do nothing, permission granted
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                                // show rationale for the notifications permission
                                mainScreenState.notificationsRationaleDialogState.show()
                            }
                            else -> {
                                // request permission
                                mainScreenState.notificationsRationaleDialogState.launchRequest()
                            }
                        }
                    }
                },
                mediaItemMapper = mediaItemMapper
            )
        }
    }

    private fun enableBootReceiver() {
        val receiver = ComponentName(this, NotificationsBootReceiver::class.java)

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun disableBootReceiver() {
        val receiver = ComponentName(this, NotificationsBootReceiver::class.java)

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun scheduleNotificationChecks() {
        (getSystemService(Context.ALARM_SERVICE) as? AlarmManager)
            ?.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME,
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
}


