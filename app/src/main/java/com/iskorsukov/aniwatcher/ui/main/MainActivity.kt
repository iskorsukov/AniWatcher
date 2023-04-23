package com.iskorsukov.aniwatcher.ui.main

import android.Manifest
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.domain.notification.alarm.NotificationsAlarmBuilder
import com.iskorsukov.aniwatcher.domain.notification.alarm.NotificationsBootReceiver
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import com.iskorsukov.aniwatcher.ui.notification.NotificationActivity
import com.iskorsukov.aniwatcher.ui.settings.SettingsCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var permissionRequestResultGranted: Boolean? by mutableStateOf(null)

        val requestNotificationsPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                permissionRequestResultGranted = isGranted
            }

        setContent {
            val uiState by mainActivityViewModel.uiState
                .collectAsStateWithLifecycle()
            val notificationsPermissionState = rememberNotificationsPermissionState(
                context = this,
                settingsRepository = settingsRepository,
                permissionRequestResultGranted = permissionRequestResultGranted
            )
            val mainScreenState = rememberMainScreenState(
                settingsRepository = settingsRepository,
                notificationsPermissionState = notificationsPermissionState
            )
            val settingsState by mainScreenState
                .settingsState
                .collectAsStateWithLifecycle()

            LaunchedEffect(
                settingsState.selectedSeasonYear
            ) {
                mainActivityViewModel.loadAiringData()
            }

            LaunchedEffect(notificationsPermissionState.launchNotificationsPermissionRequest) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && notificationsPermissionState.launchNotificationsPermissionRequest) {
                    requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            LaunchedEffect(notificationsPermissionState.notificationsPermissionGranted) {
                if (notificationsPermissionState.notificationsPermissionGranted) {
                    enableBootReceiver()
                    scheduleNotificationChecks()
                } else {
                    disableBootReceiver()
                    cancelNotificationChecks()
                }
            }

            MainScreen(
                uiState = uiState,
                mainScreenState = mainScreenState,
                settingsState = settingsState,
                onRefresh = { mainActivityViewModel.loadAiringData() },
                onStartSettings = { startSettingsActivity() },
                onStartNotifications = { startNotificationsActivity() },
                onStartDetails = { mediaItem -> startDetailsActivity(mediaItem.id) }
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


