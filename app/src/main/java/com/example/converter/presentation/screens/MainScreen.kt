package com.example.converter.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.converter.presentation.navigation.Screen
import com.example.converter.presentation.viewmodel.ConverterViewModel

@Composable
fun MainScreen(
    viewModel: ConverterViewModel = hiltViewModel()
){
    val navController = rememberNavController()
    val items = listOf(
        Screen.Exchange,
        Screen.Multi,
        Screen.Setting
    )
    Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.titleResId))},
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                                }
                            }
                        )
                    }
                }
            }
    ) {
        innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Exchange.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Exchange.route){
                ConverterScreen(
                    viewModel = viewModel,
                    onNavigateToSelectCurrency = {
                        isFrom ->
                        navController.navigate("currency_selection/$isFrom")
                    }
                )
            }
            composable(Screen.Multi.route){
                Text("zaglushka")
            }
            composable(Screen.Setting.route){
                SettingScreen(
                    viewModel = viewModel
                )
            }
            composable(
                route = "currency_selection/{isFrom}",
                arguments = listOf(navArgument("isFrom") { type = NavType.BoolType })
            ) { backStackEntry ->
                val isFrom = backStackEntry.arguments?.getBoolean("isFrom") ?: true

                CurrencySelectionScreen(
                    isFrom = isFrom,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}