package com.iskorsukov.aniwatcher.di

import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepository
import com.iskorsukov.aniwatcher.domain.notification.NotificationsRepositoryImpl
import com.iskorsukov.aniwatcher.service.AiringNotificationInteractor
import com.iskorsukov.aniwatcher.service.AiringNotificationInteractorImpl
import com.iskorsukov.aniwatcher.service.util.LocalClock
import com.iskorsukov.aniwatcher.service.util.LocalClockSystem
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

    @Binds
    @Singleton
    abstract fun bindsNotificationsRepository(
        notificationsRepositoryImpl: NotificationsRepositoryImpl
    ): NotificationsRepository

    @Binds
    abstract fun bindsLocalClick(localClockSystem: LocalClockSystem): LocalClock
}