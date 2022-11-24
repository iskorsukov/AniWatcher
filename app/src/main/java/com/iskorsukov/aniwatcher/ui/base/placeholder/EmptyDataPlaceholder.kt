package com.iskorsukov.aniwatcher.ui.base.placeholder

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iskorsukov.aniwatcher.ui.theme.*

@Composable
fun EmptyDataPlaceholder(
    @DrawableRes iconResId: Int,
    @StringRes labelResId: Int,
    @StringRes subLabelResId: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(
                    id = iconResId
                ),
                contentDescription = null,
                tint = LocalColors.current.text,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = stringResource(
                    id = labelResId
                ),
                style = LocalTextStyles.current.headline
            )
            Text(
                text = stringResource(
                    id = subLabelResId
                ),
                style = LocalTextStyles.current.headlineSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}