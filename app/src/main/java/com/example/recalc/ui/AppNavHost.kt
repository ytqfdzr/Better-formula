package com.example.recalc.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recalc.ReCalcApplication
import com.example.recalc.ui.history.HistoryScreen
import com.example.recalc.ui.history.HistoryViewModel
import com.example.recalc.ui.main.MainScreen
import com.example.recalc.ui.main.MainViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = (context.applicationContext as ReCalcApplication).container.formulaRepository

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            val viewModel: MainViewModel = viewModel(factory = MainViewModel.factory(repository))
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
