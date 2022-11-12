package com.iskorsukov.aniwatcher.di

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.airing.AiringRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiringDIModule {

    @Binds
    @Singleton
    abstract fun bindsAiringRepository(
        airingRepositoryImpl: AiringRepositoryImpl
    ): AiringRepository
}