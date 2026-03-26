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
class ConverterViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)

    private val _amountFrom = MutableStateFlow("")
    val amountFrom: StateFlow<String> = _amountFrom.asStateFlow()
    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency = userPrefsRepository.fromCurrencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "USD")
    private val _toCurrency = MutableStateFlow("EUR")
    val toCurrency = userPrefsRepository.toCurrencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()
    init {
        fetchRates("USD")
    }
    private val _favorites = MutableStateFlow<Set<String>>(setOf("USD", "EUR"))
    val favorites = userPrefsRepository.favoritesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), setOf("USD", "EUR"))
    val isCommissionEnabled = userPrefsRepository.isCommissionEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val commissionValue = userPrefsRepository.commissionValueFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.0f)

    fun toggleFavorite(currencyCode: String) {
        viewModelScope.launch {
            val currentFavorites = favorites.value.toMutableSet()
            if (currentFavorites.contains(currencyCode)) {
                currentFavorites.remove(currencyCode)
            } else {
                currentFavorites.add(currencyCode)
            }
            userPrefsRepository.saveFavorites(currentFavorites)
        }
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
            val percentage = commissionValue.value / 100.0
            result -= result * percentage
        }
        return String.format(java.util.Locale.US, "%.2f", result)
    }
    fun updateCommissionValue(newValue: String) {
        val sanitized = newValue.replace(",", ".")
        val floatValue = sanitized.toFloatOrNull()
        if(floatValue != null && floatValue >= 0f){
            viewModelScope.launch {
                userPrefsRepository.saveCommissionsValue(floatValue)
            }
        }
    }
    fun swapCurrencies() {
        viewModelScope.launch {
            val currentFrom = fromCurrency.value
            val currentTo = toCurrency.value
            userPrefsRepository.saveFromCurrency(currentTo)
            userPrefsRepository.saveToCurrency(currentFrom)
        }
    }

    fun selectCurrency(currencyCode: String, isFrom: Boolean){
        viewModelScope.launch {
            if (isFrom) {
                userPrefsRepository.saveFromCurrency(currencyCode)
            } else {
                userPrefsRepository.saveToCurrency(currencyCode)
            }
        }
    }
    fun setCommissionEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            userPrefsRepository.saveCommissionEnabled(isEnabled)
        }
    }
}