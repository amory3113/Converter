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
import net.objecthunter.exp4j.ExpressionBuilder

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
    val commissionValue = userPrefsRepository.commissionValueFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.0f)
    val isCommissionEnabled = userPrefsRepository.isCommissionEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
                        timeLastUpdated = response.timeLastUpdated,
                        isOffline = response.isOffline
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
        val sanitizedAmount = newAmount.replace(",", ".")
        if (sanitizedAmount.isEmpty() || sanitizedAmount.matches(Regex("^[0-9+\\-*/(). ]*$"))) {
            _amountFrom.value = sanitizedAmount
        }
    }
    fun calculateResult(
        amountStr: String,
        fromCurr: String,
        toCurr: String,
        rates: Map<String, Double>,
        isCommissionEnabled: Boolean,
        commissionPercent: Float
    ): String {
        val amount = try {
            ExpressionBuilder(amountStr).build().evaluate()
        } catch (e: Exception) {
            return ""
        }
        val rateFrom = rates[fromCurr] ?: 1.0
        val rateTo = rates[toCurr] ?: 1.0
        var result = (amount / rateFrom) * rateTo
        if (isCommissionEnabled) {
            val percentage = commissionPercent / 100.0
            result -= result * percentage
        }
        return String.format(java.util.Locale.US, "%.2f", result)
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