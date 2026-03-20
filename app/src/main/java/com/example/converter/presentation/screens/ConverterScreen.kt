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
import androidx.compose.runtime.setValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.converter.presentation.viewmodel.ConverterViewModel
import com.example.converter.presentation.viewmodel.CurrencyUiState
import java.util.Currency
import com.example.converter.R

@Composable
fun ConverterScreen(viewModel: ConverterViewModel = hiltViewModel()) {
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
                ConverterContent(state)
            }
        }
    }
}
@Composable
fun ConverterContent(state: CurrencyUiState.Success){
    var amountFrom by remember { mutableStateOf("1000")}
    var amountTo by remember { mutableStateOf("924.50")}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.title_converter),
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF191C1E)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = Color(0xFF4285F4),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.status_actual_offline),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF74777F)
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Column{
                CurrencyInputCard(
                    label = stringResource(R.string.label_to),
                    currencyCode = "EUR",
                    amount = amountTo,
                    onAmountChange = { amountTo = it },
                    isEditable = false
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.Center)
                    .offset(y = (-6).dp)
                    .background(Color(0xFF6750A4), CircleShape)
            ){
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Swap currencies",
                    tint = Color.White,
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        CommissionCard()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInputCard(
    label: String,
    currencyCode: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    isEditable: Boolean
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF74777F)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Surface(
                    onClick = {

                    },
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFF1F3F4)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Box(
                            modifier = Modifier
                                .size(24.dp, 16.dp)
                                .background(Color.LightGray, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = currencyCode,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF191C1E)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF44474E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                BasicTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    readOnly = !isEditable,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = if(isEditable) Color(0xFF191C1E) else Color(0xFF6750A4),
                        textAlign = TextAlign.End,
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun CommissionCard(){
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.label_commission),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF191C1E)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF1F3F4)
            ) {
                Text(
                    text = "2.0%",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF6750A4)
                )
            }
        }

    }
}