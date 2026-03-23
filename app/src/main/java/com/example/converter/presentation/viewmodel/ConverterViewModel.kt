package com.example.converter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.converter.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)

    private val _amountFrom = MutableStateFlow("")
    val amountFrom: StateFlow<String> = _amountFrom.asStateFlow()
    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency: StateFlow<String> = _fromCurrency.asStateFlow()
    private val _toCurrency = MutableStateFlow("EUR")
    val toCurrency: StateFlow<String> = _toCurrency.asStateFlow()
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()
    init {
        fetchRates("USD")
    }
    fun fetchRates(baseCurrency: String) {
        viewModelScope.launch {
            _uiState.value = CurrencyUiState.Loading
            val result = repository.getRates(baseCurrency)
            result.fold(
                onSuccess = {
                    response ->
                    _uiState.value = CurrencyUiState.Success(
                        baseCurrency = response.baseCurrency,
                        rates = response.rates,
                        timeLastUpdated = response.timeLastUpdated
                    )
                },
                onFailure = {
                    _uiState.value = CurrencyUiState.Error(
                        message = it.message ?:"Unknown error"
                    )
                }
            )
        }
    }
    fun updateAmount(newAmount: String){
        val sanitizerAmount = newAmount.replace(",", ".")
        if(sanitizerAmount.isEmpty() || sanitizerAmount.matches(Regex("^\\d*\\.?\\d*$"))){
            _amountFrom.value = sanitizerAmount
        }
    }
    fun calculateResult(rates: Map<String, Double>, isCommissionEnabled: Boolean): String{
        val amount = _amountFrom.value.toDoubleOrNull() ?: return ""
        val rateFrom = rates[_fromCurrency.value] ?: 1.0
        val rateTo = rates[_toCurrency.value] ?: 1.0
        var result = (amount / rateFrom) * rateTo
        if (isCommissionEnabled) {
            result -= result * 0.02
        }
        return String.format(java.util.Locale.US, "%.2f", result)
    }
    fun swapCurrencies() {
        val tempCurrency = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = tempCurrency
    }
}