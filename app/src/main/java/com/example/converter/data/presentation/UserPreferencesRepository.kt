package com.example.converter.data.presentation

import android.R
import android.content.Context
import android.icu.number.Precision.currency
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val FROM_CURRENCY = stringPreferencesKey("from_currency")
        val TO_CURRENCY = stringPreferencesKey("to_currency")
        val IS_COMMISSION_ENABLED = booleanPreferencesKey("is_commission_enabled")
        val FAVORITES = stringSetPreferencesKey("favorites")
        val COMMISSIONS_VALUE = floatPreferencesKey("commissions_value")
        val MULTI_BASE_CURRENCY = stringPreferencesKey("multi_base_currency")
        val MULTI_TARGET_CURRENCIES = stringSetPreferencesKey("multi_target_currency")
    }

    val fromCurrencyFlow: Flow<String> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.FROM_CURRENCY] ?: "USD"
    }

    val toCurrencyFlow: Flow<String> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.TO_CURRENCY] ?: "EUR"
    }

    val isCommissionEnabledFlow: Flow<Boolean> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.IS_COMMISSION_ENABLED] ?: false
    }

    val favoritesFlow: Flow<Set<String>> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.FAVORITES] ?: setOf("USD", "EUR")
    }
    val commissionValueFlow: Flow<Float> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.COMMISSIONS_VALUE] ?: 2.0f
    }
    val multiBaseCurrencyFlow: Flow<String> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.MULTI_BASE_CURRENCY] ?: "USD"
    }
    val multiTargetCurrenciesFlow: Flow<Set<String>> = dataStore.data.map {
        preferences -> preferences[PreferencesKeys.MULTI_TARGET_CURRENCIES] ?: setOf("EUR", "GBP", "JPY")
    }

    suspend fun saveFromCurrency(currency: String){
        dataStore.edit { preference -> preference[PreferencesKeys.FROM_CURRENCY] = currency }
    }
    suspend fun saveToCurrency(currency: String){
        dataStore.edit { preference -> preference[PreferencesKeys.TO_CURRENCY] = currency }
    }
    suspend fun saveCommissionEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.IS_COMMISSION_ENABLED] = isEnabled }
    }

    suspend fun saveFavorites(favorites: Set<String>) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.FAVORITES] = favorites }
    }
    suspend fun saveCommissionsValue(value: Float){
        dataStore.edit { preferences -> preferences[PreferencesKeys.COMMISSIONS_VALUE] = value }
    }
    suspend fun saveMultiBaseCurrency(currency: String){
        dataStore.edit { preferences -> preferences[PreferencesKeys.MULTI_BASE_CURRENCY] = currency }
    }
    suspend fun saveMultiTargetCurrencies(currencies: Set<String>) {
        dataStore.edit { preferences -> preferences[PreferencesKeys.MULTI_TARGET_CURRENCIES] = currencies }
    }
}