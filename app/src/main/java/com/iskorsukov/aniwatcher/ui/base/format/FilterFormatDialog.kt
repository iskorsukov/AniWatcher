package com.iskorsukov.aniwatcher.ui.base.format

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun FilterFormatDialog(
    filterFormatDialogState: FilterFormatDialogState
) {
    val deselectedFormats = filterFormatDialogState.deselectedFormats
    Dialog(onDismissRequest = filterFormatDialogState::dismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = LocalColors.current.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    text = stringResource(id = R.string.filter_format).uppercase(),
                    style = LocalTextStyles.current.category
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    MediaItem.LocalFormat.values().forEach { format ->
                        item {
                            TextButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    if (deselectedFormats.contains(format)) {
                                        deselectedFormats.remove(format)
                                    } else {
                                        deselectedFormats.add(format)
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                    contentDescription = null,
                                    tint = if (!deselectedFormats.contains(format)) {
                                        LocalColors.current.secondary
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(id = format.labelResId).uppercase(),
                                    style = LocalTextStyles.current.contentMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.confirm).uppercase(),
                    style = LocalTextStyles.current.contentMediumEmphasis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            filterFormatDialogState.dismiss()
                        }
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun FilterFormatDialogPreview() {
    FilterFormatDialog(
        rememberFilterFormatDialogState()
    )
}