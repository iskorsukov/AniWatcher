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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOption
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun HeaderFlowRow(
    showReset: Boolean,
    onResetClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    FlowRow(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 4.dp
    ) {
        content.invoke()
        if (showReset) {
            ResetHeaderChip {
                onResetClicked.invoke()
            }
        }
    }
}

@Composable
fun SortingOptionHeaderChip(
    selectedSortingOption: SortingOption,
    onSelectSortingOptionClicked: (() -> Unit)
) {
    val selectedSortingOptionLabel = "${stringResource(id = R.string.sort_by)}: ${stringResource(id = selectedSortingOption.labelResId).lowercase()}"

    HeaderChip(text = selectedSortingOptionLabel) {
        onSelectSortingOptionClicked.invoke()
    }
}

@Composable
fun FilterFormatHeaderChip(
    deselectedFormats: List<MediaItem.LocalFormat>,
    onFilterFormatsClicked: (() -> Unit),
) {
    var deselectedFormatsLabel = stringResource(id = R.string.filter_format)
    if (deselectedFormats.isNotEmpty()) {
        deselectedFormatsLabel += ": "
        deselectedFormats.forEach {
            deselectedFormatsLabel += stringResource(id = it.labelResId)
            deselectedFormatsLabel += ", "
        }
        deselectedFormatsLabel = deselectedFormatsLabel.removeSuffix(", ")
    }

    HeaderChip(text = deselectedFormatsLabel) {
        onFilterFormatsClicked.invoke()
    }
}

@Composable
fun ResetHeaderChip(
    onResetClicked: () -> Unit
) {
    val resetLabel = stringResource(id = R.string.reset)

    HeaderChip(text = resetLabel) {
        onResetClicked.invoke()
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

@Composable
@Preview
fun HeaderFlowRowPreview() {
    HeaderFlowRow(showReset = true, onResetClicked = { }) {
        SortingOptionHeaderChip(selectedSortingOption = SortingOption.SCORE) {

        }
        FilterFormatHeaderChip(deselectedFormats = listOf(MediaItem.LocalFormat.TV, MediaItem.LocalFormat.TV_SHORT)) {

        }
    }
}