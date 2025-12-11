package com.example.mytravel.ui.pages

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.domain.model.Rencana
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.PlanViewModel
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormPlanScreen(
    wisataId: Long,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    planViewModel: PlanViewModel = viewModel()
) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var targetDate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val addPlanState by planViewModel.addPlanResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    LaunchedEffect(addPlanState) {
        when (val result = addPlanState) {
            is UiResult.Success -> {
                planViewModel.resetAddPlanResult()
                onSaveSuccess()
            }
            is UiResult.Error -> {
                scope.launch { snackbarHostState.showSnackbar(result.message) }
                planViewModel.resetAddPlanResult()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Buat Rencana") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("Card Destinasi") // Placeholder
                    }
                }
                OutlinedTextField(value = judul, onValueChange = { judul = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = deskripsi, onValueChange = { deskripsi = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text(if (imageUri == null) "Upload Gambar" else "Ganti Gambar")
                }
                OutlinedTextField(value = targetDate, onValueChange = { targetDate = it }, label = { Text("Kapan kamu ingin mencapai ini?") }, modifier = Modifier.fillMaxWidth())
                
                Button(
                    onClick = {
                        val userId = com.example.mytravel.data.remote.SupabaseHolder.client.auth.currentUserOrNull()?.id
                        if (userId != null && judul.isNotBlank() && deskripsi.isNotBlank()) {
                            val rencana = Rencana(
                                userId = userId,
                                wisataId = wisataId,
                                judul = judul,
                                deskripsi = deskripsi,
                                targetDate = targetDate
                            )
                            val imageFile = imageUri?.let { uriToFile(context, it) }
                            planViewModel.addPlan(rencana, imageFile)
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Judul dan Deskripsi harus diisi.") }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addPlanState !is UiResult.Loading
                ) {
                    Text("Simpan")
                }
            }
            if (addPlanState is UiResult.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

// Helper function to convert Uri to File
private fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val file = File(context.cacheDir, "temp_image_plan.jpg")
    val outputStream = FileOutputStream(file)
    inputStream.copyTo(outputStream)
    return file
}
