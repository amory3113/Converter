package com.example.converter.di

import com.example.converter.data.repository.CurrencyRepositoryImpl
import com.example.converter.domain.repository.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
}