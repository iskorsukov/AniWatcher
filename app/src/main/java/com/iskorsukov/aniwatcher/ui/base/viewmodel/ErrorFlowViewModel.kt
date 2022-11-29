package com.iskorsukov.aniwatcher.ui.base.viewmodel

import com.iskorsukov.aniwatcher.ui.base.error.ErrorItem
import kotlinx.coroutines.flow.StateFlow

interface ErrorFlowViewModel {
    val errorItemFlow: StateFlow<ErrorItem?>
}