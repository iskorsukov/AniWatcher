package com.iskorsukov.aniwatcher.di

import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.airing.AiringRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AiringDIModule {

    @Binds
    abstract fun bindsAiringRepository(
        airingRepositoryImpl: AiringRepositoryImpl
    ): AiringRepository
}