package com.iskorsukov.aniwatcher.ui.sorting

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight

@Composable
fun SelectSortingOptionDialog(
    onSortingOptionSelected: (SortingOption) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = RoundedCornerShape(8.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by).uppercase(),
                    color = CardTextColorLight,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    SortingOption.values().forEach {
                        item {
                            TextButton(
                                onClick = {
                                    onSortingOptionSelected(it)
                                    onDismissRequest.invoke()
                                }
                            ) {
                                Text(
                                    text = stringResource(id = it.labelResId).uppercase(),
                                    color = CardTextColorLight
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
    SelectSortingOptionDialog(onSortingOptionSelected = {}, onDismissRequest = {})
}