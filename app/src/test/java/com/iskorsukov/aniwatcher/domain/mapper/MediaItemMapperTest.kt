package com.iskorsukov.aniwatcher.domain.mapper

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.settings.DarkModeOption
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
import com.iskorsukov.aniwatcher.domain.settings.SettingsState
import com.iskorsukov.aniwatcher.domain.util.DateTimeHelper
import com.iskorsukov.aniwatcher.domain.util.DayOfWeekLocal
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.isFollowing
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.Test

class MediaItemMapperTest {

    private val testData = mapOf(
        ModelTestDataCreator.baseMediaItem() to ModelTestDataCreator.baseAiringScheduleItemList()
    )

    private val testDataFollowed = mapOf(
        ModelTestDataCreator.baseMediaItem().isFollowing(true) to
                ModelTestDataCreator.baseAiringScheduleItemList(true)
    )

    private val testDataEmptySchedules = mapOf(
        ModelTestDataCreator.baseMediaItem() to emptyList<AiringScheduleItem>()
    )

    @Test
    fun groupAiringSchedulesByDayOfWeek() {
        val result = MediaItemMapper.groupAiringSchedulesByDayOfWeek(testData)

        assertThat(result.keys).containsExactlyElementsIn(listOf(
            DayOfWeekLocal.MONDAY,
            DayOfWeekLocal.WEDNESDAY,
            DayOfWeekLocal.SATURDAY
        ))

        val list = ModelTestDataCreator.baseAiringScheduleItemList()
        val assertValues = listOf(
            list[0],
            list[1],
            list[3]
        )
        assertThat(result.values.flatten()).containsExactlyElementsIn(assertValues)
    }

    @Test
    fun groupMediaWithNextAiringSchedule() {
        val result = MediaItemMapper.groupMediaWithNextAiringSchedule(testData)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem())
        assertThat(result.values).containsExactly(ModelTestDataCreator.baseAiringScheduleItemList()[3])
    }

    @Test
    fun groupMediaWithNextAiringSchedule_null() {
        val result = MediaItemMapper.groupMediaWithNextAiringSchedule(testDataEmptySchedules)

        assertThat(result.keys).containsExactly(ModelTestDataCreator.baseMediaItem())
        assertThat(result.values).containsExactly(null)
    }

    @Test
    fun filterExtraFollowedAiringSchedules_season() {
        val dowToAiringSchedulesMap = MediaItemMapper.groupAiringSchedulesByDayOfWeek(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.SEASON, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedAiringSchedules(dowToAiringSchedulesMap, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023))
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun filterExtraFollowedAiringSchedules_season_notFiltered() {
        val dowToAiringSchedulesMap = MediaItemMapper.groupAiringSchedulesByDayOfWeek(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.SEASON, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedAiringSchedules(dowToAiringSchedulesMap, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022))
        assertThat(result.values.flatten().size).isEqualTo(3)
    }

    @Test
    fun filterExtraFollowedAiringSchedules_week() {
        val dowToAiringSchedulesMap = MediaItemMapper.groupAiringSchedulesByDayOfWeek(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.ALL, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedAiringSchedules(dowToAiringSchedulesMap, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023))
        assertThat(result.values.flatten().size).isEqualTo(0)
    }

    @Test
    fun filterExtraFollowedAiringSchedules_week_notFiltered() {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentWeekStartToEndSeconds(any()) } returns (1667028460 to 1667933200)
        val dowToAiringSchedulesMap = MediaItemMapper.groupAiringSchedulesByDayOfWeek(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.ALL, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedAiringSchedules(dowToAiringSchedulesMap, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023))
        assertThat(result.values.flatten().size).isEqualTo(3)
        unmockkObject(DateTimeHelper)
    }

    @Test
    fun filterExtraFollowedMedia_season() {
        val mediaToAiringSchedule = MediaItemMapper.groupMediaWithNextAiringSchedule(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.SEASON, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedMedia(mediaToAiringSchedule, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023))
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun filterExtraFollowedMedia_season_notFiltered() {
        val mediaToAiringSchedule = MediaItemMapper.groupMediaWithNextAiringSchedule(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.SEASON, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedMedia(mediaToAiringSchedule, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.FALL, 2022))
        assertThat(result.size).isEqualTo(1)
    }

    @Test
    fun filterExtraFollowedMedia_week() {
        val mediaToAiringSchedule = MediaItemMapper.groupMediaWithNextAiringSchedule(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.ALL, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedMedia(mediaToAiringSchedule, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023))
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun filterExtraFollowedMedia_week_notFiltered() {
        mockkObject(DateTimeHelper)
        every { DateTimeHelper.currentWeekStartToEndSeconds(any()) } returns (1667028460 to 1667933200)
        val mediaToAiringSchedule = MediaItemMapper.groupMediaWithNextAiringSchedule(testDataFollowed)
        val settingsState = SettingsState(DarkModeOption.DARK, ScheduleType.ALL, NamingScheme.ENGLISH, true, true)
        val result = MediaItemMapper.filterExtraFollowedMedia(mediaToAiringSchedule, settingsState, DateTimeHelper.SeasonYear(DateTimeHelper.Season.WINTER, 2023))
        assertThat(result.size).isEqualTo(1)
        unmockkObject(DateTimeHelper)
    }
}