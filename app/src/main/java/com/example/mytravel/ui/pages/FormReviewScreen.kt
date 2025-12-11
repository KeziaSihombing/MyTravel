package com.example.mytravel.ui.pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.ReviewViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormReviewScreen(
    destinationId: Long,
    viewModel: ReviewViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateBack: () -> Unit
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

    LaunchedEffect(addingState) {
        if (addingState is UiResult.Success) onBack()
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Row(
            modifier = Modifier.clickable { onNavigateBack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Kembali",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Buat Review",
            fontSize = 22.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Tuliskan review...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedButton(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Photo, null)
            Spacer(Modifier.width(8.dp))
            Text("Galeri")
        }

        Text(
            text = "Preview",
            fontSize = 18.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

//        bitmap?.let {
//            Image(bitmap = it.asImageBitmap(), contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(220.dp))
//        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        } else {
            Text(
                text = "Belum Upload Gambar",
                color = Color(0xFF6200EE), // ungu
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
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
