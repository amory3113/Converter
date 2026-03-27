package com.example.converter.presentation.screens

import android.R.attr.singleLine
import android.R.id.title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.converter.R
import com.example.converter.presentation.getFlagUrl
import com.example.converter.presentation.viewmodel.ConverterViewModel
import com.example.converter.presentation.viewmodel.CurrencyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectionScreen(
    mode: String,
    onBackClick: () -> Unit,
    viewModel: ConverterViewModel,
    ){
    var searchQuery by remember { mutableStateOf("")}
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val allCurrencies = remember(uiState) {
        if (uiState is CurrencyUiState.Success) {
            (uiState as CurrencyUiState.Success).rates.keys.toList().sorted()
        } else {
            emptyList()
        }
    }
    val filteredCurrencies = allCurrencies.filter {
        it.contains(searchQuery, ignoreCase = true)
    }
    val favoriteCurrencies = filteredCurrencies.filter { favorites.contains(it)}
    val otherCurrencies = filteredCurrencies.filterNot { favorites.contains(it)}

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.title_select_currency),
                    style = MaterialTheme.typography.headlineMedium,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick){
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = {
                Text(stringResource(R.string.search_hint))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (favoriteCurrencies.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.category_favorites),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                items(favoriteCurrencies) { currencyCode ->
                    CurrencyListItem(
                        currencyCode = currencyCode,
                        isFavorite = true,
                        onFavoriteClick = { viewModel.toggleFavorite(currencyCode) },
                        onClick = {
                            when (mode) {
                                "exchange_from" -> viewModel.selectCurrency(currencyCode, true)
                                "exchange_to" -> viewModel.selectCurrency(currencyCode, false)
                                "multi_base" -> viewModel.updateMultiBaseCurrency(currencyCode)
                                "multi_add" -> viewModel.addMultiTargetCurrency(currencyCode)
                            }
                            onBackClick()
                        }
                    )
                }
            }

            if (otherCurrencies.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.category_all),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                items(otherCurrencies) { currencyCode ->
                    CurrencyListItem(
                        currencyCode = currencyCode,
                        isFavorite = false,
                        onFavoriteClick = { viewModel.toggleFavorite(currencyCode) },
                        onClick = {
                            when (mode) {
                                "exchange_from" -> viewModel.selectCurrency(currencyCode, true)
                                "exchange_to" -> viewModel.selectCurrency(currencyCode, false)
                                "multi_base" -> viewModel.updateMultiBaseCurrency(currencyCode)
                                "multi_add" -> viewModel.addMultiTargetCurrency(currencyCode)
                            }
                            onBackClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyListItem(
    currencyCode: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = getFlagUrl(currencyCode),
            contentDescription = null,
            modifier = Modifier
                .size(36.dp, 26.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currencyCode,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onFavoriteClick
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.Star,
                contentDescription = "Favorite",
                tint = if(isFavorite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}
