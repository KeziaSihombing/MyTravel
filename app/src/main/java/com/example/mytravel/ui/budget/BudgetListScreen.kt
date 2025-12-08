package com.example.mytravel.ui.budget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetListScreen(viewModel: BudgetViewModel = viewModel(), onAdd: () -> Unit) {
    val budgetItems by viewModel.budgetItems.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Budget") }) },
        floatingActionButton = {
            // Floating action button to add new items
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(budgetItems) { item ->
                BudgetItemRow(item)
            }
        }
    }
}

@Composable
fun BudgetItemRow(item: com.example.mytravel.data.model.BudgetItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title)
            Text(text = "Rp. ${item.nominal}")
        }
    }
}
