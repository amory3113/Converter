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
}