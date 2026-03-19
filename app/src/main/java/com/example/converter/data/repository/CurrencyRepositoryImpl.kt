package com.example.converter.data.repository

import com.example.converter.data.api.CurrencyApi
import com.example.converter.data.api.ExchangeRatesResponse
import com.example.converter.domain.repository.CurrencyRepository
import jakarta.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepository {

    override suspend fun getRates(baseCurrency: String): Result<ExchangeRatesResponse> {
        return try {
            val response = api.getLatestRates(baseCurrency)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}