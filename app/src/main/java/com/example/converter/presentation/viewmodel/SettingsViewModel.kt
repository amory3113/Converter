package com.example.converter.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.converter.data.presentation.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPrefsRepository: UserPreferencesRepository
) : ViewModel() {

    val commissionValue = userPrefsRepository.commissionValueFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.0f)

    fun updateCommissionValue(newValue: String) {
        val sanitized = newValue.replace(",", ".")
        val floatValue = sanitized.toFloatOrNull()
        if (floatValue != null && floatValue >= 0f) {
            viewModelScope.launch {
                userPrefsRepository.saveCommissionValue(floatValue)
            }
        }
    }
}
