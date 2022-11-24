package com.iskorsukov.aniwatcher.ui.base.topbar

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
@Preview
fun BackArrowTopBarPreview() {
    BackArrowTopAppBar("Title") {

    }
}

