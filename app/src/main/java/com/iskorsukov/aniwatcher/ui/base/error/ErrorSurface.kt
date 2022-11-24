package com.iskorsukov.aniwatcher.ui.base.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.iskorsukov.aniwatcher.ui.base.popup.PopupDialogSurface
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun ErrorSurfaceContent(
    errorItem: ErrorItem,
    onActionClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PopupDialogSurface(
        modifier = modifier,
        labelResId = errorItem.labelResId,
        subLabelResId = errorItem.subLabelResId,
        actionLabelResId = errorItem.actionLabelResId,
        dismissLabelResId = errorItem.dismissLabelResId,
        backgroundColor = LocalColors.current.error,
        onActionClicked = onActionClicked,
        onDismissRequest = onDismissRequest
    )
}

@Composable
@Preview
private fun ErrorDialogPreview() {
    ErrorSurfaceContent(
        errorItem = ErrorItem.LoadingData,
        onActionClicked = { },
        onDismissRequest = { }
    )
}