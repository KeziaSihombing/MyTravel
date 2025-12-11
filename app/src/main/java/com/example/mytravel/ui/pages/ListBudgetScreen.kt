package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.BudgetViewModel
import com.example.mytravel.ui.viewmodel.RencanaWithTotal
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBudgetScreen(
    onRencanaClick: (String) -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {
    // Mengamati state baru dari ViewModel
    val rencanaListState by budgetViewModel.rencanaWithTotals.collectAsState()

    // Memuat data awal saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        budgetViewModel.loadInitialData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val result = rencanaListState) {
                is UiResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiResult.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(result.data) { rencanaWithTotal ->
                            RencanaCard(rencana = rencanaWithTotal, onClick = { onRencanaClick(rencanaWithTotal.rencana.id.toString()) })
                        }
                    }
                }
                is UiResult.Error -> {
                    Text(
                        text = result.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RencanaCard(
    rencana: RencanaWithTotal, // Menggunakan RencanaWithTotal
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    val formattedTotal = formatter.format(rencana.total)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = rencana.rencana.judul)
            Text(text = "Rp. $formattedTotal") // Menampilkan total yang sudah diformat
        }
    }
}
