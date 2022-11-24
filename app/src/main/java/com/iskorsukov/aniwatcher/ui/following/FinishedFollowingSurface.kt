package com.iskorsukov.aniwatcher.ui.following

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.base.popup.PopupDialogSurface

@Composable
fun FinishedFollowingSurface(
    onActionClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    PopupDialogSurface(
        modifier = modifier,
        labelResId = R.string.finished_following_label,
        subLabelResId = R.string.finished_following_sub_label,
        actionLabelResId = R.string.finished_following_unfollow,
        onActionClicked = onActionClicked,
        onDismissRequest = onDismissRequest
    )
}

@Composable
@Preview
fun FinishedFollowingSurfacePreview() {
    FinishedFollowingSurface(
        onActionClicked = { },
        onDismissRequest = { }
    )
}