package com.example.mytravel.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.mytravel.ui.components.ReviewCard
import com.example.mytravel.ui.viewmodel.DestinationViewModel
import com.example.mytravel.ui.viewmodel.ReviewViewModel

@Composable
fun DestinationDetailScreen(
    destinationId: Long,
    onNavigateBack: () -> Unit,
    onNavigateAddReview: () -> Unit,
    onNavigateReviewDetail: (Long) -> Unit,
    onNavigateCommentList: (Long) -> Unit,
    onNavigateAddComment: (Long) -> Unit,
    destinationViewModel: DestinationViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val destinationState by destinationViewModel.destinationDetail.collectAsState()
    val reviewsState by reviewViewModel.reviews.collectAsState()

    LaunchedEffect(destinationId) {
        destinationViewModel.getDestination(destinationId)
        reviewViewModel.loadReviews(destinationId)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
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

        }

        when (destinationState) {

            is UiResult.Loading -> {
                item {
                    Text("Memuat detail destinasi…")
                }
            }

            is UiResult.Error -> {
                item {
                    Text("Gagal memuat destinasi")
                }
            }

            is UiResult.Success -> {
                val dest = (destinationState as UiResult.Success).data
                val mainImage = dest.imageUrls

                item {
                    Box {
                        if (mainImage != null) {
                            AsyncImage(
                                model = mainImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                            )
                        }

                        Button(
                            onClick = { /* TODO */ },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF6200EE)
                            ),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        ) {
                            Text("Buat Plan")
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        dest.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = Color(0xFF6200EE)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(dest.address)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Deskripsi", style = MaterialTheme.typography.titleSmall)
                    Text(dest.description)

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF6200EE).copy(alpha = 0.1f), // ungu transparan
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Waktu Buka", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text(dest.openHours?:"")
                        }
                        Column {
                            Text("Harga", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text(dest.price?:"")
                        }
                        Column {
                            Text("Kontak", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text(dest.contact?:"")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.Gray.copy(alpha = 0.3f))
                    )

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Review",
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Buat Review",
                            color = Color(0xFF6200EE),
                            modifier = Modifier.clickable { onNavigateAddReview() }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }

                when (reviewsState) {
                    is UiResult.Loading -> {
                        item { Text("Memuat review...") }
                    }

                    is UiResult.Error -> {
                        item { Text("Gagal memuat review") }
                    }

                    is UiResult.Success -> {
                        val reviews = (reviewsState as UiResult.Success).data

                        items(reviews) { review ->
                            ReviewCard(
                                review = review,
                                onClick = { onNavigateReviewDetail(review.id)},
                                onCommentList= {onNavigateCommentList(review.id)},
                                onNavigateAddComment = {onNavigateAddComment(review.id)}
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
