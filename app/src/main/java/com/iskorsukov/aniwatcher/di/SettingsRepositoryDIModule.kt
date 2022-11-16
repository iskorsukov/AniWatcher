package com.iskorsukov.aniwatcher.di

import com.iskorsukov.aniwatcher.domain.settings.SettingsRepository
import com.iskorsukov.aniwatcher.domain.settings.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryDIModule {

    @Binds
    @Singleton
    abstract fun bindsSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl):
            SettingsRepository
}