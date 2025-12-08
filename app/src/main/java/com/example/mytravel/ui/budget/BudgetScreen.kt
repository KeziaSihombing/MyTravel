package com.example.mytravel.ui.budget

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mytravel.ui.viewmodel.BudgetViewModel

@Composable
fun BudgetScreen() {
    val navController = rememberNavController()
    val viewModel: BudgetViewModel = viewModel()

    NavHost(navController = navController, startDestination = "budgetList") {
        composable("budgetList") {
            BudgetListScreen(
                viewModel = viewModel,
                onAdd = { navController.navigate("addBudget") }
            )
        }
        composable("addBudget") {
            AddBudgetScreen(
                viewModel = viewModel,
                onSave = { navController.popBackStack() }
            )
        }
    }
}
