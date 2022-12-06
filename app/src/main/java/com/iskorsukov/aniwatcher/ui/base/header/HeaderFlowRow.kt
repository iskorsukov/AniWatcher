package com.iskorsukov.aniwatcher.ui.base.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun HeaderFlowRow(
    selectedSortingOption: SortingOption? = null,
    onSelectSortingOptionClicked: (() -> Unit)? = null,
    deselectedFormats: List<MediaItem.LocalFormat>? = null,
    onFilterFormatsClicked: (() -> Unit)? = null,
    showReset: Boolean,
    onResetClicked: () -> Unit
) {
    var selectedSortingOptionLabel = stringResource(id = R.string.sort_by)
    if (selectedSortingOption != null) {
        selectedSortingOptionLabel += ": ${stringResource(id = selectedSortingOption.labelResId).lowercase()}"
    }

    var deselectedFormatsLabel = stringResource(id = R.string.filter_format)
    if (deselectedFormats != null && deselectedFormats.isNotEmpty()) {
        deselectedFormatsLabel += ": "
        deselectedFormats.forEach {
            deselectedFormatsLabel += stringResource(id = it.labelResId)
            deselectedFormatsLabel += ", "
        }
        deselectedFormatsLabel = deselectedFormatsLabel.removeSuffix(", ")
    }

    val resetLabel = stringResource(id = R.string.reset)

    FlowRow(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 4.dp
    ) {
        if (selectedSortingOption != null && onSelectSortingOptionClicked != null) {
            HeaderChip(text = selectedSortingOptionLabel) {
                onSelectSortingOptionClicked.invoke()
            }
        }
        if (deselectedFormats != null && onFilterFormatsClicked != null) {
            HeaderChip(text = deselectedFormatsLabel) {
                onFilterFormatsClicked.invoke()
            }
        }
        if (showReset) {
            HeaderChip(text = resetLabel) {
                onResetClicked.invoke()
            }
        }
    }
}

@Composable
private fun HeaderChip(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = LocalColors.current.onPrimary,
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .background(
                shape = RoundedCornerShape(10.dp),
                color = LocalColors.current.primary
            )
            .padding(8.dp)
    )
}