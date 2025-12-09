package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.components.BottomNavBar
import com.example.mytravel.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onNavigateToAddBudget: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddBudget) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Budget")
            }
        },
        bottomBar = {
            // Memanggil komponen BottomNavBar yang bisa digunakan kembali
            BottomNavBar(
                currentRoute = "budget", // Memberi tahu route saat ini
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        BudgetListScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
