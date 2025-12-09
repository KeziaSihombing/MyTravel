package com.example.mytravel.ui.pages

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.NavigationBar
import com.example.mytravel.ui.viewmodel.CommentViewModel
import java.io.File

@Composable
fun AddCommentScreen(
    viewModel: CommentViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onDone: () -> Unit
){
    var komentar by remember { mutableStateOf("") }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var bitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    val addingState by viewModel.adding.collectAsState()
    val context = LocalContext.current

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val tempInputStream = context.contentResolver.openInputStream(uri) ?: return@let
                val temp = File(context.cacheDir, "gallery_${System.currentTimeMillis()}.jpg")
                tempInputStream.use { ins ->
                    temp.outputStream().use { outs ->
                        ins.copyTo(outs)
                    }
                }
                imageFile = temp

                val drawableStream = context.contentResolver.openInputStream(uri)
                val drawable = android.graphics.drawable.Drawable.createFromStream(
                    drawableStream, uri.toString()
                )
                drawableStream?.close()

                drawable?.toBitmap()?.let { bmp ->
                    bitmaps = bitmaps + bmp  // <-- Tambahkan ke list!
                }
            }
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
            bmp?.let {
                val temp = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                temp.outputStream().use { os ->
                    it.compress(Bitmap.CompressFormat.JPEG, 90, os)
                }
                imageFile = temp

                bitmaps = bitmaps + it  // <-- Tambahkan ini juga
            }
        }

    LaunchedEffect(addingState) {
        if (addingState is UiResult.Success) {
            onDone()
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0254F8),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Kirim",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

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

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Balas komentar",
                fontSize = 25.sp,
                color = Color.Black
            )

            OutlinedTextField(
                value = komentar,
                onValueChange = { komentar = it },
                label = { Text("Balasan") },
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Balas komentar di sini") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {galleryLauncher.launch("image/*")},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCDD8EE),
                    contentColor = Color(0xFF3A8BFA)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = "Tambahkan foto",
                        fontSize = 18.sp
                    )
                }
            }

            ImageGrid(
                images = bitmaps,
                onRemove = { bmp -> bitmaps = bitmaps - bmp },
                modifier = Modifier.weight(1f)
            )
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGrid(
    images: List<Bitmap>,
    onRemove: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(images.size) { index ->
            ImageContainerBitmap(
                bitmap = images[index],
                onRemove = { onRemove(images[index]) }
            )
        }
    }
}

@Composable
fun ImageContainerBitmap(
    bitmap: Bitmap,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    ) {

        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
fun AsyncImages(
    model: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    coil.compose.AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}