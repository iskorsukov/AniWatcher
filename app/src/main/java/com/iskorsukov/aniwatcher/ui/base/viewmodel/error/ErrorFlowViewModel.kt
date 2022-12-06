package com.iskorsukov.aniwatcher.ui.base.viewmodel.error

import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem

interface ErrorFlowViewModel {
    fun onError(errorItem: ErrorItem?)
}