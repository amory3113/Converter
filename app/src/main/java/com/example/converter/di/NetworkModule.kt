package com.example.converter.di

import com.example.converter.data.api.CurrencyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideCurrencyApi(): CurrencyApi {
        val networkJson = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl("https://api.exchangerate-api.com/")
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CurrencyApi::class.java)
        }
    }
