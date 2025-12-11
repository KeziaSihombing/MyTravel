package com.example.mytravel.ui.pages

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatBudgetScreen(
    rencanaId: String,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {
    var judul by remember { mutableStateOf("") }
    var nominal by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val addBudgetState by budgetViewModel.addBudgetResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    LaunchedEffect(addBudgetState) {
        when (val result = addBudgetState) {
            is UiResult.Success -> {
                budgetViewModel.resetAddBudgetResult() // Reset state to avoid re-triggering
                onSaveSuccess()
            }
            is UiResult.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(result.message)
                }
                budgetViewModel.resetAddBudgetResult()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Buat Budget") },
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = judul,
                    onValueChange = { judul = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addBudgetState !is UiResult.Loading
                )

                OutlinedTextField(
                    value = nominal,
                    onValueChange = { nominal = it },
                    label = { Text("Nominal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addBudgetState !is UiResult.Loading
                )

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = addBudgetState !is UiResult.Loading
                ) {
                    Text(text = if (imageUri == null) "Unggah Gambar" else "Ganti Gambar")
                }
                imageUri?.let {
                    Text(text = "Gambar terpilih: ${it.path}")
                }

                Button(
                    onClick = {
                        val id = rencanaId.toLongOrNull()
                        val nominalDouble = nominal.toDoubleOrNull()
                        if (id != null && judul.isNotBlank() && nominalDouble != null) {
                            val imageFile = imageUri?.let { uriToFile(context, it) }
                            budgetViewModel.addBudget(id, judul, nominalDouble, imageFile)
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Judul dan Nominal harus diisi dengan benar.") }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addBudgetState !is UiResult.Loading
                ) {
                    Text("Simpan")
                }
            }

            if (addBudgetState is UiResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

private fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, "temp_image.jpg")
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    return file
}
