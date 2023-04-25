package com.iskorsukov.aniwatcher.ui.base.sorting

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
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun SelectSortingOptionDialog(
    sortingOptionsDialogState: SortingOptionsDialogState
) {
    Dialog(
        onDismissRequest = sortingOptionsDialogState::dismiss
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = LocalColors.current.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by).uppercase(),
                    style = LocalTextStyles.current.category
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    SortingOption.values().forEach {
                        item {
                            TextButton(
                                onClick = {
                                    sortingOptionsDialogState.selectedOption = it
                                    sortingOptionsDialogState.dismiss()
                                }
                            ) {
                                Text(
                                    text = stringResource(id = it.labelResId).uppercase(),
                                    style = if (it == sortingOptionsDialogState.selectedOption)
                                        LocalTextStyles.current.contentMediumEmphasis
                                    else
                                        LocalTextStyles.current.contentMedium,
                                    color = if (it == sortingOptionsDialogState.selectedOption)
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
    SelectSortingOptionDialog(
        rememberSortingOptionsDialogState()
    )
}