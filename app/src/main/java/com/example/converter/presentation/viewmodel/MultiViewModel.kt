package com.example.converter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.converter.data.presentation.UserPreferencesRepository
import com.example.converter.domain.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MultiViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()
    init {
        fetchRates("USD")
    }
    private fun fetchRates(baseCurrency: String){
        viewModelScope.launch {
            _uiState.value = CurrencyUiState.Loading
            val result = repository.getRates(baseCurrency)
            result.fold(
                onSuccess = { response ->
                    _uiState.value = CurrencyUiState.Success(
                        baseCurrency = response.baseCurrency,
                        rates = response.rates,
                        timeLastUpdated = response.timeLastUpdated,
                        isOffline = response.isOffline
                    )
                },
                onFailure = {
                    _uiState.value = CurrencyUiState.Error(message = it.message ?: "Unknown error")
                }
            )
        }
    }
    private val _multiAmount = MutableStateFlow("100")
    val multiAmount: StateFlow<String> = _multiAmount.asStateFlow()
    val multiBaseCurrency = userPrefsRepository.multiBaseCurrencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")
    val multiTargetCurrencies = userPrefsRepository.multiTargetCurrenciesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("EUR", "GBP", "JPY"))
    val isCommissionEnabled = userPrefsRepository.isCommissionEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val commissionValue = userPrefsRepository.commissionValueFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.0f)
    fun updateMultiAmount(newAmount: String) {
        val sanitizedAmount = newAmount.replace(",", ".")
        if(sanitizedAmount.isEmpty() || sanitizedAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _multiAmount.value = sanitizedAmount
        }
    }
    fun updateMultiBaseCurrency(currencyCode: String) {
        viewModelScope.launch { userPrefsRepository.saveMultiBaseCurrency(currencyCode) }
    }
    fun addMultiTargetCurrency(currencyCode: String) {
        viewModelScope.launch {
            val currencyList = multiTargetCurrencies.value.toMutableSet()
            currencyList.add(currencyCode)
            userPrefsRepository.saveMultiTargetCurrencies(currencyList)
        }
    }
    fun removeMultiTargetCurrency(currencyCode: String) {
        viewModelScope.launch {
            val currentList = multiTargetCurrencies.value.toMutableSet()
            currentList.remove(currencyCode)
            userPrefsRepository.saveMultiTargetCurrencies(currentList)
        }
    }
    fun setCommissionEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.saveCommissionEnabled(isEnabled)
        }
    }
    fun calculateMultiResult(
        amountStr: String, baseCurrency: String, targetCurrency: String,
        rates: Map<String, Double>, isCommissionEnabled: Boolean, commissionPercent: Float
    ): String {
        val amount = amountStr.toDoubleOrNull() ?: return ""
        val rateFrom = rates[baseCurrency] ?: 1.0
        val rateTo = rates[targetCurrency] ?: 1.0
        var result = (amount / rateFrom) * rateTo

        if (isCommissionEnabled) {
            val percentage = commissionPercent / 100.0
            result -= result * percentage
        }
        return String.format(java.util.Locale.US, "%.2f", result)
    }
}