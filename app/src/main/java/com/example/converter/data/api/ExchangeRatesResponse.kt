package com.example.converter.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesResponse(
    @SerialName("base")
    val baseCurrency: String,
    @SerialName("time_last_updated")
    val timeLastUpdated: Long,
    @SerialName("rates")
    val rates: Map<String, Double>
)