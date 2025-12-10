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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
            Text(
                "← Kembali",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clickable { onNavigateBack() },
                style = MaterialTheme.typography.titleMedium
            )
        }

        when (destinationState) {

            is UiResult.Loading -> {
                item {
                    Text("Loading…")
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
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black.copy(alpha = 0.5f),
                                contentColor = Color.White
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

                    Text(dest.name, style = MaterialTheme.typography.headlineSmall)

                    Text("Alamat", style = MaterialTheme.typography.titleSmall)
                    Text(dest.address)

                    Spacer(Modifier.height(8.dp))

                    Text("Waktu Buka : ${dest.openHours}")
                    Text("Harga : ${dest.price}")
                    Text("Kontak : ${dest.contact}")

                    Spacer(Modifier.height(16.dp))

                    Text("Deskripsi", style = MaterialTheme.typography.titleSmall)
                    Text(dest.description)

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Review", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Buat Review",
                            color = Color.Blue,
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
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
