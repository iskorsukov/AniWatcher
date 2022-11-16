package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.iskorsukov.aniwatcher.domain.mapper.MediaItemMapper
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.main.MainActivityUiState
import com.iskorsukov.aniwatcher.ui.main.MainActivityViewModel
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MediaScreen(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: MediaViewModel = viewModel(),
    timeInMinutesFlow: Flow<Long>,
    onMediaClicked: ((Int) -> Unit)? = null
) {
    val uiState by mainActivityViewModel
        .uiState.collectAsStateWithLifecycle()
    viewModel.onSearchTextChanged(uiState.searchText)
    viewModel.onSortingOptionChanged(uiState.sortingOption)

    val settingsState by mainActivityViewModel
        .settingsState.collectAsStateWithLifecycle()

    val mediaFlow by viewModel.mediaFlow
        .collectAsStateWithLifecycle(initialValue = emptyMap())

    val timeInMinutes by timeInMinutesFlow
        .collectAsStateWithLifecycle(initialValue = 0)

    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    val listState = rememberLazyListState()

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { mainActivityViewModel.loadAiringData() }
    ) {
        Column {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                mediaFlow.entries.forEach {
                    item {
                        MediaItemCardExtended(
                            mediaItem = it.key,
                            airingScheduleItem = it.value,
                            timeInMinutes = timeInMinutes,
                            onFollowClicked = viewModel::onFollowClicked,
                            onMediaClicked = onMediaClicked,
                            preferredNamingScheme = settingsState.preferredNamingScheme
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchField(
    onSearchTextChanged: (String) -> Unit,
    searchFieldVisibleState: MutableState<Boolean>,
    focusRequester: FocusRequester
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = text,
        onValueChange = {
            text = it
            onSearchTextChanged(it)
        },
        singleLine = true,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .focusRequester(focusRequester),
        cursorBrush = SolidColor(CardTextColorLight),
        textStyle = TextStyle(color = CardTextColorLight, fontSize = 18.sp),
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = text,
                innerTextField = innerTextField,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        text = ""
                        onSearchTextChanged("")
                        focusManager.clearFocus()
                        searchFieldVisibleState.value = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    leadingIconColor = CardTextColorLight,
                    trailingIconColor = CardTextColorLight,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                enabled = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = PaddingValues(4.dp)
            )
        }
    )
}

@Composable
@Preview
fun SearchFieldPreview() {
    val searchFieldVisibleState = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.primary)
    ) {
        if (searchFieldVisibleState.value) {
            AnimatedVisibility(
                visible = searchFieldVisibleState.value,
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                SearchField(
                    onSearchTextChanged = { },
                    searchFieldVisibleState = searchFieldVisibleState,
                    focusRequester = focusRequester
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        } else {
            IconButton(onClick = { searchFieldVisibleState.value = true }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
@Preview
fun MediaScreenPreview() {
    val mediaFlow by flowOf(
        MediaItemMapper.groupMediaWithNextAiringSchedule(
            mapOf(
                ModelTestDataCreator.baseMediaItem() to
                        ModelTestDataCreator.baseAiringScheduleItemList()
            )
        )
    ).collectAsStateWithLifecycle(initialValue = emptyMap())

    val uiState by flowOf(MainActivityUiState(false, null))
        .collectAsStateWithLifecycle(MainActivityUiState(false, null))

    val timeInMinutes = 27785710L

    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    val searchFieldVisibleState = remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colors.primary)
            ) {
                if (searchFieldVisibleState.value) {
                    AnimatedVisibility(
                        visible = searchFieldVisibleState.value,
                        enter = expandHorizontally(expandFrom = Alignment.Start),
                        exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
                    ) {
                        SearchField(
                            onSearchTextChanged = { },
                            searchFieldVisibleState = searchFieldVisibleState,
                            focusRequester = focusRequester
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                } else {
                    IconButton(onClick = { searchFieldVisibleState.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                mediaFlow.entries.forEach {
                    item {
                        MediaItemCardExtended(
                            mediaItem = it.key,
                            airingScheduleItem = it.value,
                            timeInMinutes = timeInMinutes,
                            onFollowClicked = { },
                            onMediaClicked = { }
                        )
                    }
                }
            }
        }
    }
}