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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iskorsukov.aniwatcher.ui.theme.PrimaryColor
import com.iskorsukov.aniwatcher.ui.theme.TextColorLight

@Composable
fun BackArrowTopAppBar(onBackButtonClicked: () -> Unit) {
    TopAppBar(backgroundColor = PrimaryColor) {
        IconButton(onClick = onBackButtonClicked) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
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
        cursorBrush = SolidColor(TextColorLight),
        textStyle = TextStyle(color = TextColorLight, fontSize = 18.sp),
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
                    leadingIconColor = TextColorLight,
                    trailingIconColor = TextColorLight,
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