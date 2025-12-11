package com.example.mytravel.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytravel.domain.model.DiaryEntry
import com.example.mytravel.ui.viewmodel.DetailDiaryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailDiaryScreen(
    diaryId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: DetailDiaryViewModel = viewModel()
) {
    val diary by viewModel.diary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(diaryId) {
        viewModel.loadDiary(diaryId)
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            onNavigateBack()
            viewModel.resetDeleteSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Diary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    diary?.let {
                        IconButton(onClick = { onNavigateToEdit(it.id!!) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color.Red
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (diary == null) {
                Text(
                    text = "Diary tidak ditemukan",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                DiaryDetailContent(diary = diary!!)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Diary") },
            text = { Text("Apakah kamu yakin ingin menghapus diary ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        diary?.id?.let { viewModel.deleteDiary(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus", color = Color.Red)
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

@Composable
fun DiaryDetailContent(diary: DiaryEntry) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header dengan warna
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(parseColor(diary.color))
        ) {
            Text(
                text = diary.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (diary.color == "#000000") Color.White else Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 16.dp)
            )
        }

        // Tanggal
        Text(
            text = formatDate(diary.createdAt.toString()),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Gambar
        if (!diary.imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = diary.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = diary.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}