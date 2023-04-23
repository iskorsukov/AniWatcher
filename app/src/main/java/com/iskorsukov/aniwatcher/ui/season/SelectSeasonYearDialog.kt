package com.iskorsukov.aniwatcher.ui.season

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.main.SeasonYearDialogState
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun SelectSeasonYearDialog(
    seasonYearDialogState: SeasonYearDialogState
) {
    val selectedSeasonYear by seasonYearDialogState.selectedSeasonYear
        .collectAsState()

    Dialog(onDismissRequest = { seasonYearDialogState.showSelectSeasonYearDialog = false }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = LocalColors.current.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.select_season).uppercase(),
                    style = LocalTextStyles.current.category
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    seasonYearDialogState.seasonYearOptions.forEach { seasonYear ->
                        item {
                            TextButton(
                                onClick = {
                                    seasonYearDialogState.onSeasonYearSelected(seasonYear)
                                    seasonYearDialogState.showSelectSeasonYearDialog = false
                                }
                            ) {
                                Text(
                                    text = if (seasonYear == DateTimeHelper.SeasonYear.THIS_WEEK)
                                        stringResource(id = R.string.schedule_this_week).uppercase()
                                    else
                                        "${seasonYear.season.name} ${seasonYear.year}",
                                    style = if (seasonYear == selectedSeasonYear)
                                        LocalTextStyles.current.contentMediumEmphasis
                                    else
                                        LocalTextStyles.current.contentMedium,
                                    color = if (seasonYear == selectedSeasonYear)
                                        LocalColors.current.secondary
                                    else
                                        Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}