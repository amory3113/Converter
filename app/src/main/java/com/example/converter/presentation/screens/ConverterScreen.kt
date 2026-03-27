package com.example.converter.presentation.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.converter.presentation.viewmodel.ConverterViewModel
import com.example.converter.presentation.viewmodel.CurrencyUiState
import java.util.Currency
import com.example.converter.R
import com.example.converter.presentation.getFlagUrl

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel(), onNavigateToSelectCurrency: (Boolean) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ){
        when (val state = uiState){
            is CurrencyUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            is CurrencyUiState.Error -> {
                Text("Error")
            }
            is CurrencyUiState.Success -> {
                ConverterContent(state, viewModel, onNavigateToSelectCurrency)
            }
            is CurrencyUiState.Success -> {
                ConverterContent(state, viewModel, onNavigateToSelectCurrency)
            }
        }
    }
}
@Composable
fun ConverterContent(state: CurrencyUiState.Success, viewModel: ConverterViewModel, onCurrencyClick: (Boolean) -> Unit){
    val amountFrom by viewModel.amountFrom.collectAsState()
    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val toCurrency by viewModel.toCurrency.collectAsState()
    val isCommissionEnabled by viewModel.isCommissionEnabled.collectAsState()
    val commissionValue by viewModel.commissionValue.collectAsState()
    val amountTo = viewModel.calculateResult(
        amountStr = amountFrom,
        fromCurr = fromCurrency,
        toCurr = toCurrency,
        rates = state.rates,
        isCommissionEnabled = isCommissionEnabled,
        commissionPercent = commissionValue
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.title_converter),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.status_actual_offline),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Column{
                CurrencyInputCard(
                    label = stringResource(R.string.label_from),
                    currencyCode = fromCurrency,
                    amount = amountFrom,
                    onAmountChange = { viewModel.updateAmount(it) },
                    onCurrencyClick = { onCurrencyClick(true) },
                    isEditable = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                CurrencyInputCard(
                    label = stringResource(R.string.label_to),
                    currencyCode = toCurrency,
                    amount = amountTo,
                    onAmountChange = { },
                    onCurrencyClick = { onCurrencyClick(false) },
                    isEditable = false,
                    amountColor = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = { viewModel.swapCurrencies() },
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.Center)
                    .zIndex(1f)
                    .background(Color(0xFF6750A4), CircleShape)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.ic_swap_vertical),
                    contentDescription = "Swap currencies",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        CommissionCard(
            isCommissionEnabled = isCommissionEnabled,
            commissionValue = commissionValue,
            onCheckedChange = {viewModel.setCommissionEnabled(it)}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInputCard(
    label: String,
    currencyCode: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onCurrencyClick: () -> Unit,
    isEditable: Boolean,
    amountColor: Color = MaterialTheme.colorScheme.onSurface
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Surface(
                    onClick = onCurrencyClick,
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        AsyncImage(
                            model = getFlagUrl(currencyCode),
                            contentDescription = "$currencyCode flag",
                            modifier = Modifier
                                .size(28.dp, 20.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currencyCode,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                BasicTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    readOnly = !isEditable,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = amountColor,
                        textAlign = TextAlign.End,
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd){
                            if(amount.isEmpty()){
                                Text(
                                    text = "0",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), // Полупрозрачный цвет
                                        textAlign = TextAlign.End,
                                        fontSize = 32.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CommissionCard(
    isCommissionEnabled: Boolean,
    commissionValue: Float,
    onCheckedChange: (Boolean) -> Unit
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.label_commission) + " ($commissionValue%)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
            Switch(
                checked = isCommissionEnabled,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

