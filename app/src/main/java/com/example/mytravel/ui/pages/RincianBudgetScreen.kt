package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.domain.model.Budget
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RincianBudgetScreen(
    rencanaId: String,
    onAddBudget: (String) -> Unit,
    onEditBudget: (Long) -> Unit, 
    onNavigateBack: () -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {
    val id = rencanaId.toLongOrNull()
    val budgetItemsState by budgetViewModel.budgetItems.collectAsState()
    val deleteState by budgetViewModel.deleteBudgetResult.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var budgetToDelete by remember { mutableStateOf<Budget?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        id?.let {
            budgetViewModel.listenForBudgetUpdates(it)
        }
    }

    // Listener untuk notifikasi delete
    LaunchedEffect(deleteState) {
        when (val result = deleteState) {
            is UiResult.Success -> {
                scope.launch { snackbarHostState.showSnackbar("Budget berhasil dihapus") }
                budgetViewModel.resetDeleteBudgetResult()
            }
            is UiResult.Error -> {
                scope.launch { snackbarHostState.showSnackbar(result.message) }
                budgetViewModel.resetDeleteBudgetResult()
            }
            else -> {}
        }
    }
    
    if (showDeleteDialog && budgetToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus item '${budgetToDelete!!.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        budgetViewModel.deleteBudget(budgetToDelete!!.id ?: 0L)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // SnackbarHost ditambahkan
        topBar = {
            TopAppBar(
                title = { Text("Rincian Budget") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = { 
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Card Destinasi") // Placeholder
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "List Budget",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onAddBudget(rencanaId) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Budget")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                 when (val result = budgetItemsState) {
                    is UiResult.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is UiResult.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(result.data) { budget ->
                                BudgetItemRow(
                                    budget = budget,
                                    onEditClick = { onEditBudget(budget.id ?: 0L) },
                                    onDeleteClick = {
                                        budgetToDelete = budget
                                        showDeleteDialog = true
                                    }
                                )
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
}

@Composable
fun BudgetItemRow(
    budget: Budget,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formatter = NumberFormat.getNumberInstance(Locale("in", "ID"))
    val formattedNominal = formatter.format(budget.nominal)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = budget.title)
            Text(text = "Rp. $formattedNominal")
        }
        Row {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
