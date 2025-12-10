package com.example.mytravel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mytravel.domain.model.Review

@Composable
fun ReviewCard(
    review: Review,
    onClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = review.userName,
            style = MaterialTheme.typography.titleMedium
        )

        val previewText = if (review.content.length > 120) {
            review.content.take(120) + "..."
        } else {
            review.content
        }

        Text(
            text = previewText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        Text(
            text = "Lihat selengkapnya",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                onClick(review.id)
            }
        )
    }
}

