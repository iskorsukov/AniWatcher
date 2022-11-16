package com.iskorsukov.aniwatcher.ui.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

            if (mediaItemToAiringSchedules != null) {
                DetailsScreen(
                    timeInMinutesFlow = timeInMinutesFlow,
                    mediaItem = mediaItemToAiringSchedules!!.first,
                    airingScheduleList = mediaItemToAiringSchedules!!.second
                        .sortedBy { it.airingAt },
                    preferredNamingScheme = settingsState.preferredNamingScheme
                )
            }
        }
    }

    companion object {
        const val MEDIA_ITEM_ID_EXTRA = "media_item_id_extra"
    }
}