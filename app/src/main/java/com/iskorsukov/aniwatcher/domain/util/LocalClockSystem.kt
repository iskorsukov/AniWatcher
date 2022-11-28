package com.iskorsukov.aniwatcher.domain.util

import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocalClockSystem @Inject constructor() {
    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun currentTimeSeconds(): Int {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
    }
}