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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.base.topbar.BackArrowTopAppBar
import com.iskorsukov.aniwatcher.ui.theme.AniWatcherTheme
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsActivity: ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaItemId = intent.getIntExtra(MEDIA_ITEM_ID_EXTRA, -1)
        if (mediaItemId == -1) finish()

        viewModel.loadMediaWithAiringSchedules(mediaItemId)

        setContent {
            val data by viewModel.dataFlow.collectAsStateWithLifecycle()
            val detailsScreenState = rememberDetailsScreenState(
                detailsScreenData = data,
                settingsRepository = settingsRepository
            )
            val mediaItemHasBanner = detailsScreenState.detailsScreenData
                .mediaItemWithSchedules?.first?.bannerImageUrl != null

            val settingsState by detailsScreenState.settingsState
                .collectAsStateWithLifecycle()

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
                    DetailsScreen(
                        detailsScreenState = detailsScreenState,
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