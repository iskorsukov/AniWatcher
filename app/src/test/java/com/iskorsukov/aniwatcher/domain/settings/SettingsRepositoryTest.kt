package com.iskorsukov.aniwatcher.domain.settings

import android.content.Context
import android.content.SharedPreferences
import com.google.common.truth.Truth.assertThat
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
        every { context.getString(any()) } returns "some_key"
        every { sharedPreferences.getString(any(), any()) } returns NamingScheme.ENGLISH.name

        settingsRepository = SettingsRepositoryImpl(context, sharedPreferences)

        var state = settingsRepository.settingsStateFlow.value
        assertThat(state.preferredNamingScheme).isEqualTo(NamingScheme.ENGLISH)

        every { sharedPreferences.getString(any(), any()) } returns NamingScheme.ROMAJI.name
        settingsRepository.onPreferenceChanged()

        state = settingsRepository.settingsStateFlow.value
        assertThat(state.preferredNamingScheme).isEqualTo(NamingScheme.ROMAJI)
    }
}