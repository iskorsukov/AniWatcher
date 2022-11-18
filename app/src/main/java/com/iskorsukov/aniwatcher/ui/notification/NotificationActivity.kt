package com.iskorsukov.aniwatcher.ui.notification

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.iskorsukov.aniwatcher.ui.details.DetailsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity: ComponentActivity() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationsScreen(
                notificationsViewModel = notificationsViewModel,
                onNotificationClicked = this::startDetailsActivity
            )
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