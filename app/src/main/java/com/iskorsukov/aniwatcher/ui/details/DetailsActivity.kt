package com.iskorsukov.aniwatcher.ui.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.ui.base.topbar.BackArrowTopAppBar
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalLifecycleComposeApi::class)
@AndroidEntryPoint
class DetailsActivity: ComponentActivity() {

    private val viewModel: DetailsViewModel by viewModels()

    private val timeInMinutesFlow = flow {
        while (true) {
            val timeInMillis = System.currentTimeMillis()
            emit(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
            delay(TimeUnit.SECONDS.toMillis(10))
        }
    }.distinctUntilChanged()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaItemId = intent.getIntExtra(MEDIA_ITEM_ID_EXTRA, -1)
        if (mediaItemId == -1) finish()

        setContent {
            val settingsState by viewModel.settingsState
                .collectAsStateWithLifecycle()

            val mediaItemToAiringSchedules by viewModel.getMediaWithAiringSchedules(mediaItemId)
                .collectAsStateWithLifecycle(null)

            AniWatcherTheme {
                Scaffold(
                    topBar = {
                        BackArrowTopAppBar {
                            finish()
                        }
                    },
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    if (mediaItemToAiringSchedules != null) {
                        DetailsScreen(
                            timeInMinutesFlow = timeInMinutesFlow,
                            mediaItem = mediaItemToAiringSchedules!!.first,
                            airingScheduleList = mediaItemToAiringSchedules!!.second
                                .sortedBy { it.airingAt },
                            preferredNamingScheme = settingsState.preferredNamingScheme,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    private fun navigateToAniList(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    companion object {
        const val MEDIA_ITEM_ID_EXTRA = "media_item_id_extra"
    }
}