package com.example.mytravel.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material3.Icon
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
    onClick: (Long) -> Unit,
    onCommentList: (Long) -> Unit,
    onNavigateAddComment: () -> Unit,
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

        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Lihat selengkapnya",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onClick(review.id)
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lihat komentar",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable{
                    onCommentList(review.id)
                },
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.Comment,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onNavigateAddComment() },
            )
        }
    }
}

