package com.example.converter.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.converter.presentation.viewmodel.ConverterViewModel
import com.example.converter.presentation.viewmodel.CurrencyUiState

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState){
            is CurrencyUiState.Loading -> {
               CircularProgressIndicator()
            }
            is CurrencyUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: ${state.message}", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchRates("USD")}) {
                        Text("Repeat request")
                    }
                }
            }
            is CurrencyUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Base currency: ${state.baseCurrency}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.rates.toList()) { (currency, rate) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ){
                                Row(
                                    modifier = Modifier.padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = currency, style = MaterialTheme.typography.titleMedium)
                                    Text(text = rate.toString(), style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}