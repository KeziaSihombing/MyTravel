package com.example.mytravel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BudgetItem(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("user_id")
    val userId: String,
    @SerialName("title")
    val title: String,
    @SerialName("nominal")
    val nominal: Double,
    @SerialName("image_url") // Diganti dari attachmentUrl
    val imageUrl: String? = null
)
