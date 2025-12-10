package com.example.mytravel.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val id: Long,
    val user_id: String,
    val wisata_id: Long,
    val review: String,
    val gambar: String? = null,
    val created_at: String,
    val updated_at: String
)

data class Review(
    val id: Long,
    val userId: String,
    val userName: String,
    val destinationId: Long,
    val content: String,
    val images: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Serializable
data class ReviewInsertDto(
    @SerialName("user_id") val userId: String,
    @SerialName("wisata_id") val wisataId: Long,
    @SerialName("review") val review: String,
    @SerialName("gambar") val gambar: String? = null
)
