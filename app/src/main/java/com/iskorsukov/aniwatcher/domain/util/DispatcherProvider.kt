package com.iskorsukov.aniwatcher.domain.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DispatcherProvider {

    fun io(): CoroutineDispatcher = Dispatchers.IO
}