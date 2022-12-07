package com.iskorsukov.aniwatcher.ui.details

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
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

            val mediaItemHasBanner = mediaItemToAiringSchedules?.first?.bannerImageUrl != null

            AniWatcherTheme(settingsState.darkModeOption) {
                Scaffold(
                    topBar = {
                        if (!mediaItemHasBanner) {
                            BackArrowTopAppBar {
                                finish()
                            }
                        }
                    },
                    backgroundColor = LocalColors.current.background
                ) { innerPadding ->
                    val primaryColorInt = LocalColors.current.primary.toArgb()
                    if (mediaItemToAiringSchedules != null) {
                        DetailsScreen(
                            timeInMinutesFlow = timeInMinutesFlow,
                            mediaItem = mediaItemToAiringSchedules!!.first,
                            airingScheduleList = mediaItemToAiringSchedules!!.second
                                .sortedBy { it.airingAt },
                            preferredNamingScheme = settingsState.preferredNamingScheme,
                            modifier = Modifier.padding(innerPadding),
                            onBackButtonClicked = { finish() },
                            onLearnMoreClicked = {
                                navigateToAniList(
                                    it,
                                    primaryColorInt
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun navigateToAniList(url: String, @ColorInt toolbarColor: Int) {
        val builder = CustomTabsIntent.Builder()
        val defaultColors = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor)
            .build()
        builder.setDefaultColorSchemeParams(defaultColors)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    companion object {
        const val MEDIA_ITEM_ID_EXTRA = "media_item_id_extra"
    }
}