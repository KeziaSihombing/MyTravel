package com.example.mytravel.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CommentDto (
    val id: Long,
    val user_id: String,
    val review_id: Long,
    val komentar: String,
    val likes: Int? = null,
    val gambar: List<String>? = emptyList(),
    val created_at: String,
    val updated_at: String
)

data class Comment (
    val id: Long,
    val userId: String,
    val reviewId: Long,
    val komentar: String,
    val likes: Int?,
    val gambar: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant
)