package com.example.converter.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.converter.R

sealed class Screen(
    val route: String,
    @StringRes val titleResId: Int,
    val icon: ImageVector
) {
    object Exchange : Screen("exchange", R.string.tab_exchange, Icons.Default.Refresh)
    object Multi : Screen("multi", R.string.tab_multi, Icons.AutoMirrored.Filled.List)
    object Setting : Screen("setting", R.string.tab_settings, Icons.Default.Settings)
}