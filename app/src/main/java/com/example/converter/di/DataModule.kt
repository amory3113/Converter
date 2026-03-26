package com.example.converter.di

import android.content.Context
import com.example.converter.data.presentation.UserPreferencesRepository
import com.example.converter.data.presentation.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ) : UserPreferencesRepository {
        return UserPreferencesRepository(context.dataStore)
    }
}
