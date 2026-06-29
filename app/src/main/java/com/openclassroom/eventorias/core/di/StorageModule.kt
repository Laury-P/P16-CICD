package com.openclassroom.eventorias.core.di

import android.content.Context
import com.openclassroom.eventorias.core.data.SettingsStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun provideSettingsStorage(
        @ApplicationContext context: Context
    ): SettingsStorage {
       return  SettingsStorage(context)
    }
}