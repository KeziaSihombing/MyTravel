package com.example.mytravel.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("nominal")
    val nominal: Double,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("rencana_id") // Field baru ditambahkan
    val rencanaId: Long? = null
)
