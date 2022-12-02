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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import java.util.*

@Composable
fun SelectSeasonYearDialog(
    onSeasonYearSelected: (DateTimeHelper.SeasonYear) -> Unit,
    onDismissRequest: () -> Unit,
    selectedSeasonYear: DateTimeHelper.SeasonYear
) {
    val calendar = Calendar.getInstance()
    val currentSeasonYear = DateTimeHelper.currentSeasonYear(calendar)
    var season = currentSeasonYear.season
    val items = mutableListOf<DateTimeHelper.SeasonYear>()
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

    Dialog(onDismissRequest = onDismissRequest) {
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
                    items.forEach { seasonYear ->
                        item {
                            TextButton(
                                onClick = {
                                    onSeasonYearSelected.invoke(seasonYear)
                                    onDismissRequest.invoke()
                                }
                            ) {
                                Text(
                                    text = "${seasonYear.season.name} ${seasonYear.year}",
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

@Composable
@Preview
fun SelectSortingOptionDialogPreview() {
    SelectSeasonYearDialog(
        onSeasonYearSelected = { },
        onDismissRequest = { },
        selectedSeasonYear = DateTimeHelper.currentSeasonYear(Calendar.getInstance())
    )
}