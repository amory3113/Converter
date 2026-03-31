package com.example.converter.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val code: String,
    val rate: Double,
    val lastUpdate: Long
)