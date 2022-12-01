package com.iskorsukov.aniwatcher.di

import android.content.Context
import androidx.room.Room
import com.iskorsukov.aniwatcher.data.room.MediaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDIModule {

    @Provides
    @Singleton
    fun providesMediaDatabase(@ApplicationContext context: Context): MediaDatabase {
        return Room.databaseBuilder(
            context, MediaDatabase::class.java, "MediaDatabase"
        ).build()
    }
}