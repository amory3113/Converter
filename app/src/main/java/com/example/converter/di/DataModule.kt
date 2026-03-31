package com.example.converter.di

import android.content.Context
import androidx.room.Room
import com.example.converter.data.local.CurrencyDao
import com.example.converter.data.local.CurrencyDatabase
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
    @Provides
    @Singleton
    fun provideCurrencyDatabase(
        @ApplicationContext context: Context
    ) : CurrencyDatabase {
        return Room.databaseBuilder(
            context,
            CurrencyDatabase:: class.java,
            "currency_db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideCurrencyDao(
        database: CurrencyDatabase
    ) : CurrencyDao {
        return database.dao
    }
}
