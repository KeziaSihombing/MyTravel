package com.example.mytravel.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ProfileDto (
    val id: String,
    val name: String,
    val description: String? = null,
    val created_at: String,
    val updated_at: String
)

data class Profile (
    val id: String,
    val name: String,
    val email: String,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)