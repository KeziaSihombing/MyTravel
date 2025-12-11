package com.example.mytravel.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.ReviewViewModel
import io.ktor.websocket.Frame

@Composable
fun DetailReviewScreen(
    reviewId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ReviewViewModel = viewModel()
) {
    val detailState by viewModel.reviewDetail.collectAsState()

    LaunchedEffect(reviewId) {
        viewModel.loadReviewDetail(reviewId)
    }

    when (val state = detailState) {
        is UiResult.Loading -> {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                CircularProgressIndicator()
            }
        }

        is UiResult.Error -> {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = state.message,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
        }

        is UiResult.Success -> {
            val review = state.data

            Column(
                modifier = Modifier
                    .padding(20.dp)
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


                Spacer(Modifier.height(16.dp))

                // Centered image, bigger
                if (!review.images.isNullOrEmpty()) {
                    AsyncImage(
                        model = review.images,
                        contentDescription = null,
                        modifier = Modifier
                            .size(320.dp) // lebih gede
                            .padding(bottom = 8.dp)
                            .align(Alignment.CenterHorizontally) // ketengahin
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Box ungu muda untuk konten
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .background(
                            color = Color(0xFFEEE5FF),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Dibuat oleh: ${review.userName}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = review.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        else -> {}
    }
}
