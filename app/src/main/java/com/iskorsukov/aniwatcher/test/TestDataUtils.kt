package com.iskorsukov.aniwatcher.test

import java.util.Calendar

fun String.toSeconds(): Int {
    // example string: 7.10.2022/11:20
    val tokens = this.split("/")
    if (tokens.isEmpty()) return 0
    val dateTokens = tokens[0].split(".")
    val timeTokens = tokens.getOrNull(1)?.split(":")
    val calendar = Calendar.getInstance()
    calendar.apply {
        set(Calendar.YEAR, dateTokens[2].toInt())
        set(Calendar.MONTH, dateTokens[1].toInt() - 1)
        set(Calendar.DAY_OF_MONTH, dateTokens[0].toInt())
    }
    if (!timeTokens.isNullOrEmpty()) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, timeTokens[0].toInt())
            set(Calendar.MINUTE, timeTokens[1].toInt())
            set(Calendar.SECOND, 0)
        }
    }
    return (calendar.timeInMillis / 1000).toInt()
}