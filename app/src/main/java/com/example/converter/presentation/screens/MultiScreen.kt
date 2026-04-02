package com.example.converter.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.converter.R
import com.example.converter.presentation.getFlagUrl
import com.example.converter.presentation.viewmodel.CurrencyUiState
import com.example.converter.presentation.viewmodel.MultiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiScreen(
    viewModel: MultiViewModel,
    onSelectBaseCurrency: () -> Unit = {},
    onAddTargetCurrency: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val baseCurrency by viewModel.multiBaseCurrency.collectAsState()
    val targetCurrencies by viewModel.multiTargetCurrencies.collectAsState()
    val multiAmount by viewModel.multiAmount.collectAsState()
    val isCommissionEnabled by viewModel.isCommissionEnabled.collectAsState()
    val commissionValue by viewModel.commissionValue.collectAsState()
    var isKeyboardVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    isKeyboardVisible = false
                    focusManager.clearFocus()
                })
            }
    ) {
        Text(
            text = stringResource(R.string.multi_exchange_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 32.sp,
            modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 16.dp)
        )
        MultiBaseCurrencyCard(
            currencyCode = baseCurrency,
            amount = multiAmount,
            onAmountChange = { viewModel.updateMultiAmount(it) },
            onClick = onSelectBaseCurrency,
            onInputClick = { isKeyboardVisible = true }
        )
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ){
            CommissionCard(
                isCommissionEnabled = isCommissionEnabled,
                commissionValue = commissionValue,
                onCheckedChange = { viewModel.setCommissionEnabled(it) }
            )
        }

        Text(
            text = stringResource(R.string.conversion),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState is CurrencyUiState.Success) {
                val rates = (uiState as CurrencyUiState.Success).rates

                items(targetCurrencies.toList()) { currencyCode ->
                    val resultAmount = viewModel.calculateMultiResult(
                        amountStr = multiAmount,
                        baseCurrency = baseCurrency,
                        targetCurrency = currencyCode,
                        rates = rates,
                        isCommissionEnabled = isCommissionEnabled,
                        commissionPercent = commissionValue
                    )
                    TargetCurrencyCard(
                        currencyCode = currencyCode,
                        amount = resultAmount,
                        onDelete = { viewModel.removeMultiTargetCurrency(currencyCode) }
                    )
                }
            }
            item {
                TextButton(
                    onClick = onAddTargetCurrency,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.add_target_currency), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (isKeyboardVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                isKeyboardVisible = false
                focusManager.clearFocus()
            },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        ) {
            CustomKeyboard(
                onKeyClick = { symbol -> viewModel.appendMultiAmount(symbol) },
                onBackspace = { viewModel.backspaceMultiAmount() },
                onClear = { viewModel.clearMultiAmount() },
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )
        }
    }
}


@Composable
fun MultiBaseCurrencyCard(
    currencyCode: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    onClick: () -> Unit,
    onInputClick: () -> Unit = {}
) {
    val blueGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF5A75FF), Color(0xFF4356FF))
    )
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = amount, selection = TextRange(amount.length)))
    }
    LaunchedEffect(amount) {
        if (amount != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(
                text = amount,
                selection = TextRange(amount.length)
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(blueGradient)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.multi_base_currency),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onClick,
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = getFlagUrl(currencyCode),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp, 16.dp).clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currencyCode,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                Box(
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { newValue ->
                            textFieldValue = newValue
                            onAmountChange(newValue.text)
                        },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                if (textFieldValue.text.isEmpty()) {
                                    Text(
                                        text = "0",
                                        fontSize = 28.sp,
                                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White.copy(alpha = 0.5f))
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onInputClick()
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun TargetCurrencyCard(currencyCode: String, amount: String, onDelete: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = getFlagUrl(currencyCode),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp, 22.dp).clip(RoundedCornerShape(3.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = currencyCode,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove currency",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) // Полупрозрачный серый
                    )
                }
            }
        }
    }
}