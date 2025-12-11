package com.example.mytravel.ui.pages




import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytravel.ui.viewmodel.ListDiaryViewModel
import com.example.mytravel.domain.model.DiaryEntry
import com.example.mytravel.ui.viewmodel.DetailDiaryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDiaryScreen(
    onNavigateToBuat: () -> Unit,
    viewModel: ListDiaryViewModel = viewModel(),
    viewModel2: DetailDiaryViewModel = viewModel()
) {
    val diaries by viewModel.diaries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDiaries()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Travel Diary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToBuat,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Diary")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                diaries.isEmpty() -> {
                    Text(
                        text = "Belum ada diary. Yuk buat yang pertama!",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            diaries,
                            key = { it.id ?: it.hashCode() }
                        ) { diary ->
                            DiaryCard(
                                diary = diary,
                                onDelete = {
                                    diary.id?.let { idSafe ->
                                        viewModel.deleteDiary(idSafe)
                                    }
                                },
                                onClick = {
                                    diary.id?.let { idSafe ->
                                        viewModel2.loadDiary(idSafe)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}




@Composable
fun DiaryCard(
    diary: DiaryEntry,
    onDelete: () -> Unit,
    onClick: (id: Int) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }




    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(parseColor(diary.color))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clickable{
                        onClick(diary.id!!)
                    }
            ) {
                // Image
                if (!diary.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = diary.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }




                // Title
                Text(
                    text = diary.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )




                Spacer(modifier = Modifier.height(4.dp))




                // Content
                Text(
                    text = diary.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }




            // Delete button
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }




    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Diary") },
            text = { Text("Apakah kamu yakin ingin menghapus diary ini?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
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
}




fun parseColor(colorString: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorString))
    } catch (e: Exception) {
        Color.White
    }
}