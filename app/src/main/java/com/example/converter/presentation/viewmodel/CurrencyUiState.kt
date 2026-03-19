package com.example.converter.presentation.viewmodel

sealed class CurrencyUiState {
    object Loading : CurrencyUiState()
    data class Success(
        val baseCurrency: String,
        val rates: Map<String, Double>,
        val timeLastUpdated: Long
    ) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}