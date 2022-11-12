package com.iskorsukov.aniwatcher.di

import com.iskorsukov.aniwatcher.service.AiringNotificationInteractor
import com.iskorsukov.aniwatcher.service.AiringNotificationInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationDIModule {

    @Binds
    @Singleton
    abstract fun bindsAiringNotificationInteractor(
        airingNotificationInteractorImpl: AiringNotificationInteractorImpl
    ): AiringNotificationInteractor
}