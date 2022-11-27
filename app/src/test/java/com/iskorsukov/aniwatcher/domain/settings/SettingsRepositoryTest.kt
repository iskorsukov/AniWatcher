package com.iskorsukov.aniwatcher.domain.settings

import android.content.Context
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.R
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {

    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    private lateinit var settingsRepository: SettingsRepository

    @Test
    fun settingsStateFlow() = runTest {
        every { context.getString(R.string.settings_naming_scheme_key) } returns "naming_scheme"
        every { context.getString(R.string.settings_schedule_type_key) } returns "schedule_type"
        every { sharedPreferences.getString("naming_scheme", any()) } returns NamingScheme.ENGLISH.name
        every { sharedPreferences.getString("schedule_type", any()) } returns ScheduleType.ALL.name

        settingsRepository = SettingsRepositoryImpl(context, sharedPreferences)

        var state = settingsRepository.settingsStateFlow.value
        assertThat(state.preferredNamingScheme).isEqualTo(NamingScheme.ENGLISH)

        every { sharedPreferences.getString("naming_scheme", any()) } returns NamingScheme.ROMAJI.name
        settingsRepository.onPreferenceChanged()

        every { sharedPreferences.getString("schedule_type", any()) } returns ScheduleType.SEASON.name
        settingsRepository.onPreferenceChanged()

        state = settingsRepository.settingsStateFlow.value
        assertThat(state.preferredNamingScheme).isEqualTo(NamingScheme.ROMAJI)
        assertThat(state.scheduleType).isEqualTo(ScheduleType.SEASON)
    }
}