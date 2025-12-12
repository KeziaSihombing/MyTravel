package com.example.mytravel.ui.pages

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.PlanViewModel
import java.io.File
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    destinationId: Long,
    destinationName: String,
    destinationImageUrl: String?,
    viewModel: PlanViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val addingState by viewModel.adding.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher Galeri
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch(Dispatchers.IO) {
                val tempInputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val temp = File(context.cacheDir, "gallery_${System.currentTimeMillis()}.jpg")
                tempInputStream.use { ins ->
                    temp.outputStream().use { outs -> ins.copyTo(outs) }
                }
                imageFile = temp // Simpan File


                val drawableStream = context.contentResolver.openInputStream(uri)
                val drawable = android.graphics.drawable.Drawable.createFromStream(drawableStream, uri.toString())
                val bmp = drawable?.toBitmap()
                launch(Dispatchers.Main) { bitmap = bmp }
                drawableStream?.close()
            }
        }
    }

    // Launcher Kamera
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        bmp?.let {
            scope.launch(Dispatchers.IO) {
                // Simpan hasil kamera ke file
                val temp = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                temp.outputStream().use { os ->
                    it.compress(Bitmap.CompressFormat.JPEG, 90, os)
                }
                launch(Dispatchers.Main) {
                    imageFile = temp
                    bitmap = it
                }
            }
        }
    }

    //  Observer Status Simpan
    LaunchedEffect(addingState) {
        if (addingState is UiResult.Loading) {
            isLoading = true
        } else if (addingState is UiResult.Success) {
            isLoading = false
            viewModel.resetAddingState()
            onNavigateBack()
        } else if (addingState is UiResult.Error) {
            isLoading = false
        }
    }

    // Tampilan UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Rencana Baru", fontSize = 18.sp) },
                windowInsets = WindowInsets(top = 0.dp),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(top = 0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (destinationImageUrl != null) {
                        AsyncImage(
                            model = destinationImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                    }
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))

                    Text(
                        text = destinationName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    )
                }
            }

            Text("Judul Rencana", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: Liburan Keluarga") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Text("Deskripsi", fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Tulis rencana kegiatanmu secara detail di sini...") },
                maxLines = 5,
                shape = RoundedCornerShape(8.dp)
            )

            Text("Lampiran", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Photo, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Galeri")
                }
                OutlinedButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kamera")
                }
            }

            bitmap?.let {
                Card(modifier = Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(12.dp)) {
                    Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }

            if (addingState is UiResult.Error) {
                Text(
                    text = (addingState as UiResult.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tombol Simpan
            Button(
                onClick = {
                    viewModel.addPlan(
                        destinationId = destinationId,
                        title = title,
                        description = description,
                        imageFile = imageFile
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = title.isNotEmpty() && description.isNotEmpty() && !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Rencana")
                }
            }
        }
    }
}