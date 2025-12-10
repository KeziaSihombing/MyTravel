package com.example.mytravel.ui.pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.ReviewViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormReviewScreen(
    destinationId: Long,
    viewModel: ReviewViewModel = viewModel(),
    onBack: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current
    val addingState by viewModel.adding.collectAsState()

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult

        val inputStream = context.contentResolver.openInputStream(uri) ?: return@rememberLauncherForActivityResult

        val mime = context.contentResolver.getType(uri) ?: "image/*"
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "img"

        val temp = File(context.cacheDir, "image_${System.currentTimeMillis()}.$extension")

        inputStream.use { ins ->
            temp.outputStream().use { outs ->
                ins.copyTo(outs)
            }
        }

        imageFile = temp

        bitmap = BitmapFactory.decodeFile(temp.absolutePath)
    }


    // Camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview())
    { bmp-> bmp?.let {
        val temp = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        temp.outputStream().use { os ->
            it.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, os)
        }
        imageFile = temp
        bitmap = it
        }
    }

    // Auto pop when success
    LaunchedEffect(addingState) {
        if (addingState is UiResult.Success) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tambah Review") })
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Tuliskan review...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Photo, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Galeri")
                }
                OutlinedButton(onClick = { cameraLauncher.launch(null) }) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Kamera")
                }
            }

            // Preview images
            bitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp))
            }

            Button(
                onClick = {
                    viewModel.addReview(destinationId, content, imageFile)
                },
                enabled = content.isNotBlank() && addingState !is UiResult.Loading,
            ) {
                Text(if (addingState is UiResult.Loading) "Mengirim..." else "Kirim Review")
            }

            if (addingState is UiResult.Error) {
                Text(
                    (addingState as UiResult.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
