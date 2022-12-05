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
import com.iskorsukov.aniwatcher.ui.sorting.SortingOption
import com.iskorsukov.aniwatcher.ui.theme.LocalColors

@Composable
fun HeaderFlowRow(
    selectedSortingOption: SortingOption,
    onSelectSortingOptionClicked: () -> Unit
) {
    FlowRow(
        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 4.dp
    ) {
        HeaderChip(
            text = "${stringResource(id = R.string.sort_by)}: ${stringResource(id = selectedSortingOption.labelResId).lowercase()}"
        ) {
            onSelectSortingOptionClicked.invoke()
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