package com.iskorsukov.aniwatcher.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem

@Composable
fun rememberDetailsScreenState(
    detailsScreenData: DetailsScreenData,
    settingsRepository: SettingsRepository
): DetailsScreenState {
    return remember(
        detailsScreenData,
        settingsRepository
    ) {
        DetailsScreenState(
            detailsScreenData = detailsScreenData,
            settingsRepository = settingsRepository
        )
    }
}

class DetailsScreenState(
    val detailsScreenData: DetailsScreenData,
    settingsRepository: SettingsRepository
) {
    val settingsState = settingsRepository.settingsStateFlow
}

data class DetailsScreenData(
    val mediaItemWithSchedules: Pair<MediaItem, List<AiringScheduleItem>>? = null,
    val timeInMinutes: Long = 0L,
    val errorItem: ErrorItem? = null
)