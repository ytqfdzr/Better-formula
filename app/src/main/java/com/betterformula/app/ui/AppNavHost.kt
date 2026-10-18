package com.betterformula.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.betterformula.app.BetterFormulaApplication
import com.betterformula.app.ui.history.HistoryScreen
import com.betterformula.app.ui.history.HistoryViewModel
import com.betterformula.app.ui.main.MainScreen
import com.betterformula.app.ui.main.MainViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as BetterFormulaApplication).container
    val repository = container.formulaRepository
    val settingsRepository = container.settingsRepository

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(repository, settingsRepository))
            MainScreen(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate("history") },
            )
        }
        composable("history") {
            val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(repository))
            HistoryScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}
