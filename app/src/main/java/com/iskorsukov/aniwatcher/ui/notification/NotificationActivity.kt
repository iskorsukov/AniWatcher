package com.iskorsukov.aniwatcher.ui.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import com.iskorsukov.aniwatcher.ui.base.topbar.BackArrowTopAppBar
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : ComponentActivity() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        BackArrowTopAppBar {
                            finish()
                        }
                    },
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    NotificationsScreen(
                        notificationsViewModel = notificationsViewModel,
                        onNotificationClicked = this::startDetailsActivity,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun startDetailsActivity(mediaId: Int) {
        startActivity(
            Intent(this, DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.MEDIA_ITEM_ID_EXTRA, mediaId)
            }
        )
    }
}