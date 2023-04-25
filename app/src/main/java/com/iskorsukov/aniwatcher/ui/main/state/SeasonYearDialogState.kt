package com.iskorsukov.aniwatcher.ui.main.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

@Composable
fun rememberSeasonYearDialogState(
    settingsRepository: SettingsRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SeasonYearDialogState {
    return remember(
        settingsRepository
    ) {
        SeasonYearDialogState(
            settingsRepository,
            coroutineScope
        )
    }
}

class SeasonYearDialogState(
    val settingsRepository: SettingsRepository,
    coroutineScope: CoroutineScope
) {
    var showSelectSeasonYearDialog by mutableStateOf(false)
        private set

    val selectedSeasonYear = settingsRepository.settingsStateFlow
        .map { it.selectedSeasonYear }
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            settingsRepository.settingsStateFlow.value.selectedSeasonYear
        )

    val seasonYearOptions: List<DateTimeHelper.SeasonYear>
        get() {
            val calendar = Calendar.getInstance()
            val currentSeasonYear = DateTimeHelper.currentSeasonYear(calendar)
            var season = currentSeasonYear.season
            val items = mutableListOf<DateTimeHelper.SeasonYear>()
            items.add(DateTimeHelper.SeasonYear.THIS_WEEK)
            items.add(currentSeasonYear)
            for (i in 1..3) {
                season = DateTimeHelper.Season.values()[
                        if (season.ordinal - 1 >= 0) {
                            season.ordinal - 1
                        } else {
                            season.ordinal + 3
                        }
                ]
                val year = if (season.ordinal > currentSeasonYear.season.ordinal)
                    currentSeasonYear.year - 1
                else
                    currentSeasonYear.year
                items.add(DateTimeHelper.SeasonYear(season, year))
            }
            return items.toList()
        }

    fun onSeasonYearSelected(selectedSeasonYear: DateTimeHelper.SeasonYear) {
        settingsRepository.setSelectedSeasonYear(selectedSeasonYear)
    }

    fun show() {
        showSelectSeasonYearDialog = true
    }

    fun dismiss() {
        showSelectSeasonYearDialog = false
    }
}