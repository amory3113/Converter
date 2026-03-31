package com.example.converter.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyDao {
    @Query("SELECT * from currency_rates")
    suspend fun getAllRates(): List<CurrencyRateEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)
    @Query("DELETE FROM currency_rates")
    suspend fun clearRates()
}