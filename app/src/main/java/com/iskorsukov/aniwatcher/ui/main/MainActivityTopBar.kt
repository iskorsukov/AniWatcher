package com.iskorsukov.aniwatcher.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.ui.base.topbar.SearchField
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun TopBar(
    mainScreenState: MainScreenState,
    mainActivityUiState: MainActivityUiState,
    onSettingsClicked: () -> Unit,
    onNotificationsClicked: () -> Unit,
    onSelectSeasonYearClicked: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    val settingsState by mainScreenState.settingsState.collectAsStateWithLifecycle()
    val unreadNotifications = mainActivityUiState.unreadNotificationsCount

    TopAppBar(backgroundColor = LocalColors.current.primary) {
        if (mainScreenState.screen?.hasSearchBar == true) {
            AnimatedSearchField(
                searchFieldState = mainScreenState.searchFieldState,
                focusRequester = focusRequester
            )
        }
        if (mainScreenState.screen?.hasSeasonYear == true) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        onSelectSeasonYearClicked.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (settingsState.selectedSeasonYear == DateTimeHelper.SeasonYear.THIS_WEEK)
                            stringResource(id = R.string.schedule_this_week).uppercase()
                        else
                            "${settingsState.selectedSeasonYear.season.name} ${settingsState.selectedSeasonYear.year}",
                    color = LocalColors.current.onPrimary,
                    fontSize = 18.sp
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_arrow_drop_down_24),
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
            }
        }
        Spacer(
            Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        IconButton(onClick = onNotificationsClicked) {
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_notifications_24),
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
                if (unreadNotifications > 0) {
                    Text(
                        text = unreadNotifications.coerceAtMost(99).toString(),
                        style = LocalTextStyles.current.contentSmallEmphasisWhite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                LocalColors.current.attentionBackground,
                                CircleShape
                            )
                            .padding(horizontal = 2.dp)
                    )
                }
            }
        }
        IconButton(onClick = onSettingsClicked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_settings_24),
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
    }
}

@Composable
private fun AnimatedSearchField(
    searchFieldState: SearchFieldState,
    focusRequester: FocusRequester
) {
    AnimatedVisibility(
        visible = searchFieldState.searchFieldOpen,
        enter = expandHorizontally(expandFrom = Alignment.Start),
        exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
    ) {
        SearchField(
            searchText = searchFieldState.searchText,
            onSearchTextChanged = { searchFieldState.searchText = it },
            onSearchCancelled = {
                searchFieldState.searchFieldOpen = false
            },
            focusRequester = focusRequester
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    if (!searchFieldState.searchFieldOpen) {
        IconButton(
            onClick = {
                searchFieldState.searchFieldOpen = true
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
    }
}

@Composable
@Preview
private fun AnimatedSearchFieldPreview() {
    val searchFieldState = rememberSearchFieldState()
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LocalColors.current.primary)
    ) {
        if (searchFieldState.searchFieldOpen) {
            AnimatedSearchField(
                searchFieldState = searchFieldState,
                focusRequester = focusRequester
            )
        } else {
            IconButton(
                onClick = {
                    searchFieldState.searchFieldOpen = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
            }
        }
    }
}