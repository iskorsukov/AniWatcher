package com.iskorsukov.aniwatcher.ui.base.error

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.exception.ApolloException
import com.iskorsukov.aniwatcher.domain.exception.RoomException

sealed class ErrorItem(
    @StringRes val labelResId: Int,
    @StringRes val subLabelResId: Int?,
    @StringRes val actionLabelResId: Int?,
    val action: Action = Action.DISMISS,
    @StringRes val dismissLabelResId: Int?
) {
    object LoadingData: ErrorItem(
        labelResId = R.string.loading_error_label,
        subLabelResId = R.string.loading_error_sub_label,
        actionLabelResId = R.string.try_again,
        action = Action.REFRESH,
        dismissLabelResId = null
    )

    object StoringData: ErrorItem(
        labelResId = R.string.storing_error_label,
        subLabelResId = R.string.storing_error_sub_label,
        actionLabelResId = null,
        dismissLabelResId = R.string.dismiss
    )

    object Unknown: ErrorItem(
        R.string.unknown_error_label,
        R.string.unknown_error_sub_label,
        actionLabelResId = null,
        dismissLabelResId = R.string.dismiss
    )

    enum class Action {
        REFRESH,
        DISMISS
    }

    companion object {
        fun ofThrowable(e: Throwable): ErrorItem {
            return when (e) {
                is ApolloException -> LoadingData
                is RoomException -> StoringData
                else -> Unknown
            }
        }
    }
}
