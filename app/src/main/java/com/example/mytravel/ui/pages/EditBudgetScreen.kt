package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.domain.model.Budget
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(
    budgetId: Long,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {
    val budgetState by budgetViewModel.budgetToEdit.collectAsState()
    val updateState by budgetViewModel.updateBudgetResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(budgetId) {
        budgetViewModel.getBudgetDetails(budgetId)
    }

    // Listener untuk hasil update
    LaunchedEffect(updateState) {
        when (updateState) {
            is UiResult.Success -> {
                budgetViewModel.resetUpdateBudgetResult() // Reset state
                onSaveSuccess() // Kembali setelah sukses
            }
            is UiResult.Error -> {
                scope.launch { snackbarHostState.showSnackbar((updateState as UiResult.Error).message) }
                budgetViewModel.resetUpdateBudgetResult()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Budget") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = { 
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val result = budgetState) {
                is UiResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiResult.Error -> {
                    Text(result.message, modifier = Modifier.align(Alignment.Center))
                }
                is UiResult.Success -> {
                    val budget = result.data
                    if (budget == null) {
                        Text("Budget tidak ditemukan", modifier = Modifier.align(Alignment.Center))
                    } else {
                        EditBudgetForm(budget, budgetViewModel, updateState is UiResult.Loading)
                    }
                }
            }
        }
    }
}

@Composable
fun EditBudgetForm(
    budget: Budget,
    budgetViewModel: BudgetViewModel,
    isLoading: Boolean
) {
    var judul by remember { mutableStateOf(budget.title) }
    var nominal by remember { mutableStateOf(budget.nominal.toString()) }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = judul,
            onValueChange = { judul = it },
            label = { Text("Judul") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = nominal,
            onValueChange = { nominal = it },
            label = { Text("Nominal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Button(
            onClick = {
                val nominalDouble = nominal.toDoubleOrNull()
                if (judul.isNotBlank() && nominalDouble != null) {
                    budget.id?.let { budgetViewModel.updateBudget(it, judul, nominalDouble) }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Simpan Perubahan")
            }
        }
    }
}
