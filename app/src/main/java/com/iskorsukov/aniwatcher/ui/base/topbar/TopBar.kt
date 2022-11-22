package com.iskorsukov.aniwatcher.ui.base.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun BackArrowTopAppBar(
    title: String? = null,
    onBackButtonClicked: () -> Unit,
) {
    TopAppBar(
        backgroundColor = LocalColors.current.primary,
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = LocalTextStyles.current.topBarTitle
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = LocalColors.current.onPrimary
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchField(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onSearchCancelled: () -> Unit,
    focusRequester: FocusRequester
) {
    val focusManager = LocalFocusManager.current

    val searchTextFieldValue = remember(searchText) {
        mutableStateOf(
            TextFieldValue(
                text = searchText,
                selection = TextRange(searchText.length)
            )
        )
    }

    BasicTextField(
        value = searchTextFieldValue.value,
        onValueChange = {
            searchTextFieldValue.value = it
            if (it.text != searchText) {
                onSearchTextChanged(it.text)
            }
        },
        singleLine = true,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(LocalColors.current.background, RoundedCornerShape(16.dp))
            .focusRequester(focusRequester),
        cursorBrush = SolidColor(LocalColors.current.text),
        textStyle = TextStyle(color = LocalColors.current.text, fontSize = 18.sp),
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = searchText,
                innerTextField = innerTextField,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = {
                        onSearchTextChanged("")
                        focusManager.clearFocus()
                        onSearchCancelled.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    leadingIconColor = LocalColors.current.text,
                    trailingIconColor = LocalColors.current.text,
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
    val searchText = remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(LocalColors.current.primary)
    ) {
        if (searchFieldVisibleState.value) {
            AnimatedVisibility(
                visible = searchFieldVisibleState.value,
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                SearchField(
                    searchText = searchText.value,
                    onSearchTextChanged = { searchText.value = it },
                    onSearchCancelled = { searchFieldVisibleState.value = false },
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
                    tint = LocalColors.current.onPrimary
                )
            }
        }
    }
}