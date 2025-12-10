package com.example.mytravel.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.CircleAvatar
import com.example.mytravel.ui.components.getInitials
import com.example.mytravel.ui.viewmodel.CommentViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.util.Log

@Composable
fun CommentDetailScreen(
    commentId: Long,
    viewModel: CommentViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.commentDetail.collectAsState()
    LaunchedEffect(commentId) {
        viewModel.getCommentDetail(commentId)
    }

    when(state){
        is UiResult.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator(
                    strokeWidth = 30.dp,
                )
            }
        }
        is UiResult.Success -> {
            val comment = (state as UiResult.Success).data

            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 30.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onNavigateBack() },
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
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    CircleAvatar(
                        initials = getInitials(comment.userName).uppercase(),
                        color = Color(0xFF7E57C2),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = comment.userName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = comment.komentar,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                            .withZone(ZoneId.systemDefault()) // zona waktu lokal
                        val time = formatter.format(comment.createdAt)

                        Text(text = time, fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    CommentImages(comment.gambar)
                }
            }
        }
        is UiResult.Error -> {
            Text("Error: ${(state as UiResult.Error).message}")
        }
    }
}
@Composable
fun CommentImages(images: List<String>) {
    if (images.isEmpty()) {
        Log.d("CommentImages", "No images found for this comment")
        return
    }

    Log.d("CommentImages", "IMAGE COUNT = ${images.size}")
    images.forEachIndexed { index, url ->
        Log.d("CommentImages", "[$index] URL = $url")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        images.forEach { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}


