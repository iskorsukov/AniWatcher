package com.iskorsukov.aniwatcher.ui.base

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R

sealed class ErrorItem(
    @StringRes val labelResId: Int,
    @StringRes val subLabelResId: Int?
) {
    object LoadingData: ErrorItem(R.string.loading_error_label, R.string.loading_error_sub_label)
}
