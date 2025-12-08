package com.example.mytravel.ui.budget

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mytravel.ui.viewmodel.BudgetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(viewModel: BudgetViewModel = viewModel(), onSave: () -> Unit) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            viewModel.imageUri.value = uri
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                viewModel.setImageBytes(inputStream?.readBytes())
            }
        }
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Budget") }) }
    ) {
        Column(modifier = Modifier.padding(it).padding(16.dp)) {
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.nominal.value,
                onValueChange = { viewModel.nominal.value = it },
                label = { Text("Nominal") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { 
                photoPickerLauncher.launch(
                    PickVisualMedia.Request(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Unggah Gambar")
            }

            viewModel.imageUri.value?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Selected image",
                    modifier = Modifier.size(128.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.addBudgetItem()
                onSave()
            }) {
                Text("Simpan")
            }
        }
    }
}
