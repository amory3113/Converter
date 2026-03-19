package com.example.converter.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {
    @GET("v4/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRatesResponse
}