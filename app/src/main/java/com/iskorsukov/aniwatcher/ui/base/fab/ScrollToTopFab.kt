package com.iskorsukov.aniwatcher.ui.base.fab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScrollToTopFab(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState
) {
    val shouldShowFab by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 5
        }
    }

    AnimatedVisibility(
        visible = shouldShowFab,
        modifier = modifier
    ) {
        FloatingActionButton(
            backgroundColor = LocalColors.current.primary,
            onClick = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_upward_24),
                contentDescription = null,
                tint = LocalColors.current.onPrimary
            )
        }
    }
}