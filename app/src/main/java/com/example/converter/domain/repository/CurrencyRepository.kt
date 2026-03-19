package com.example.converter.domain.repository

import com.example.converter.data.api.ExchangeRatesResponse

interface CurrencyRepository{
    suspend fun getRates(baseCurrency: String): Result<ExchangeRatesResponse>
}