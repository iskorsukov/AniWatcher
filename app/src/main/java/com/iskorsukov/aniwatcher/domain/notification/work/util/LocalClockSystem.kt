package com.iskorsukov.aniwatcher.domain.notification.work.util

import javax.inject.Inject

class LocalClockSystem @Inject constructor() {
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}