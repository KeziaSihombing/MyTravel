package com.example.mytravel.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PlanDto(
    val id: Long,
    val user_id : String,
    val wisata_id : Long,
    val judul : String,
    val deskripsi : String,
    val gambar : String? = null,
    val created_at : String
)

data class Plan(
    val id: Long,
    val userId : String,
    val destinationId : Long,
    val title : String,
    val content : String,
    val images : String?,
    val createdAt : Instant
)

@Serializable
data class PlanInsertDto(
    val user_id : String,
    val wisata_id : Long,
    val judul : String,
    val deskripsi : String,
    val gambar : String? = null
)
