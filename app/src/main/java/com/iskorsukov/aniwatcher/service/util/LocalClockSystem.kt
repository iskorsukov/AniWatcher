package com.iskorsukov.aniwatcher.service.util

import javax.inject.Inject

class LocalClockSystem @Inject constructor(): LocalClock {
    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}