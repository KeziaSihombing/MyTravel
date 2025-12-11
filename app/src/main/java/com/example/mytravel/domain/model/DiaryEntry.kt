package com.example.mytravel.domain.model




import kotlinx.serialization.Serializable
import java.time.Instant




@Serializable
data class DiaryEntryDto(
    val id: Int? = null,
    val title: String,
    val content: String,
    val image_url: String? = null,
    val color: String = "#FFFFFF",
    val created_at: String,
    val updated_at: String? = null
)




// Domain Model untuk digunakan di aplikasi
data class DiaryEntry(
    val id: Int? = null,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val color: String = "#FFFFFF",
    val createdAt: Instant,
    val updatedAt: Instant? = null
)