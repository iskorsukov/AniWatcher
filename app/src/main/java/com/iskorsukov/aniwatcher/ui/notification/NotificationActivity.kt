package com.iskorsukov.aniwatcher.ui.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.base.topbar.BackArrowTopAppBar
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class NotificationActivity : ComponentActivity() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()

    private val timeInMinutesFlow = flow {
        while (true) {
            val timeInMillis = System.currentTimeMillis()
            emit(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }.distinctUntilChanged()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        BackArrowTopAppBar(stringResource(id = R.string.notifications)) {
                            finish()
                        }
                    },
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    NotificationsScreen(
                        notificationsViewModel = notificationsViewModel,
                        timeInMinutesFlow = timeInMinutesFlow,
                        onNotificationClicked = this::startDetailsActivity,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        notificationsViewModel.resetNotificationsCounter()
        notificationsViewModel.cancelStatusBarNotifications()
    }

    private fun startDetailsActivity(mediaId: Int) {
        startActivity(
            Intent(this, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.MEDIA_ITEM_ID_EXTRA, mediaId)
            }
        )
    }
}