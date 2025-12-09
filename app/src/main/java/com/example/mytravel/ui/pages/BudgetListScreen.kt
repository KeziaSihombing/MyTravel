package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mytravel.data.model.BudgetItem
import com.example.mytravel.ui.viewmodel.BudgetViewModel

@Composable
fun BudgetListScreen(
    viewModel: BudgetViewModel,
    modifier: Modifier = Modifier // Modifier ditambahkan
) {
    val budgetItems by viewModel.budgetItems.collectAsState()

    // Scaffold dan TopAppBar dihapus
    LazyColumn(modifier = modifier) { // Modifier diterapkan di sini
        items(budgetItems) { item ->
            BudgetItemRow(item)
        }
    }
}

@Composable
fun BudgetItemRow(item: BudgetItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title)
            Text(text = "Rp. ${item.nominal}")
        }
    }
}
