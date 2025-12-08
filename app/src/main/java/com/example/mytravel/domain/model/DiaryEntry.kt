package com.example.mytravel.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DiaryEntry(
    val id: Int? = null,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val color: String = "#FFFFFF",
    val createdAt: String,
    val updatedAt: String? = null
)