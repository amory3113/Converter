package com.example.converter.data.repository

import com.example.converter.data.api.CurrencyApi
import com.example.converter.data.api.ExchangeRatesResponse
import com.example.converter.data.local.CurrencyDao
import com.example.converter.data.local.CurrencyRateEntity
import com.example.converter.domain.repository.CurrencyRepository
import jakarta.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi,
    private val dao: CurrencyDao
) : CurrencyRepository {

    override suspend fun getRates(baseCurrency: String): Result<ExchangeRatesResponse> {
        return try {
            val response = api.getLatestRates(baseCurrency)
            val entities = response.rates.map { (code, rate) ->
                CurrencyRateEntity(
                    code = code,
                    rate = rate,
                    lastUpdate = response.timeLastUpdated
                )
            }
            dao.insertRates(entities)
            Result.success(response)
        } catch (e: Exception) {
            val cacheRates = dao.getAllRates()
            if(cacheRates.isNotEmpty()){
                val ratesMap = cacheRates.associate { it.code to it.rate }
                val lastUpdated = cacheRates.first().lastUpdate
                val cacheResponse = ExchangeRatesResponse(
                    baseCurrency = baseCurrency,
                    rates = ratesMap,
                    timeLastUpdated = lastUpdated,
                    isOffline = true
                )
                Result.success(cacheResponse)
            } else {
            Result.failure(e)
            }
        }
    }
}