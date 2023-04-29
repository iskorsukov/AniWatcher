package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.toSeconds
import org.junit.Test
import java.util.Locale
import java.util.concurrent.TimeUnit

class AiringScheduleItemTest {

    @Test
    fun getAiringAtFormatted() {
        Locale.setDefault(Locale.US)

        val airingScheduleItem = ModelTestDataCreator.airingScheduleItem(
            id = 1,
            airingAt = "7.10.2022/18:00".toSeconds(),
            episode = 1
        )

        assertThat(airingScheduleItem.getAiringAtDateTimeFormatted()).isEqualTo("October 07, 18:00")
    }

    @Test
    fun getAiringInFormatted() {
        val testMinutes = TimeUnit.SECONDS.toMinutes("6.10.2022/12:15".toSeconds().toLong())

        val airingScheduleItem = ModelTestDataCreator.airingScheduleItem(
            id = 1,
            airingAt = "7.10.2022/18:00".toSeconds(),
            episode = 1
        )

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("1 days, 5 hours, 45 minutes")
    }

    @Test
    fun getAiringInFormatted_noDays() {
        val testMinutes = TimeUnit.SECONDS.toMinutes("7.10.2022/12:15".toSeconds().toLong())

        val airingScheduleItem = ModelTestDataCreator.airingScheduleItem(
            id = 1,
            airingAt = "7.10.2022/18:00".toSeconds(),
            episode = 1
        )

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("5 hours, 45 minutes")
    }

    @Test
    fun getAiringInFormatted_noHours() {
        val testMinutes = TimeUnit.SECONDS.toMinutes("6.10.2022/17:15".toSeconds().toLong())

        val airingScheduleItem = ModelTestDataCreator.airingScheduleItem(
            id = 1,
            airingAt = "7.10.2022/18:00".toSeconds(),
            episode = 1
        )

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("1 days, 45 minutes")
    }

    @Test
    fun getAiringInFormatted_noMinutes() {
        val testMinutes = TimeUnit.SECONDS.toMinutes("6.10.2022/12:00".toSeconds().toLong())

        val airingScheduleItem = ModelTestDataCreator.airingScheduleItem(
            id = 1,
            airingAt = "7.10.2022/18:00".toSeconds(),
            episode = 1
        )

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isEqualTo("1 days, 6 hours")
    }

    @Test
    fun getAiringInFormatted_null() {
        val testMinutes = Long.MAX_VALUE

        val airingScheduleItem = ModelTestDataCreator.airingScheduleItem(
            id = 1,
            airingAt = "7.10.2022/18:00".toSeconds(),
            episode = 1
        )

        assertThat(airingScheduleItem.getAiringInFormatted(testMinutes)).isNull()
    }
}