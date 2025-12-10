package com.example.mytravel.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mytravel.domain.model.CommentWithUserName
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun CommentItem(
    comment: CommentWithUserName,
    modifier: Modifier = Modifier,
    onNavigateCommentDetail: (id: Long) -> Unit
) {
    Row(
        modifier = modifier
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

            Text(text = if(comment.komentar.length > 50) comment.komentar.take(50) + "..." else comment.komentar, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Lihat Selengkapnya",
                color = Color.Blue,
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    onNavigateCommentDetail(comment.id)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))

            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                .withZone(ZoneId.systemDefault()) // zona waktu lokal
            val time = formatter.format(comment.createdAt)

            Text(text = time, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}


fun getInitials(text: String): String {
    return text
        .split(" ")
        .filter { it.isNotBlank() }
        .map { it.first() }
        .joinToString("")
}

