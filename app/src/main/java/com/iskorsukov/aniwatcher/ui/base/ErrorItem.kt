package com.iskorsukov.aniwatcher.ui.base

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R

sealed class ErrorItem(
    @StringRes val labelResId: Int,
    @StringRes val subLabelResId: Int?,
    @StringRes val actionLabelResId: Int?,
    val action: Action = Action.DISMISS,
    @StringRes val dismissLabelResId: Int? = null
) {
    object LoadingData: ErrorItem(
        R.string.loading_error_label,
        R.string.loading_error_sub_label,
        R.string.try_again,
        Action.REFRESH
    )

    enum class Action {
        REFRESH,
        DISMISS
    }
}
