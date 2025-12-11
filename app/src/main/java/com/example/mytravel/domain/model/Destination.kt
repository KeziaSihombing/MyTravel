package com.example.mytravel.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class DestinationDto(
    val id: Long,
    val nama_wisata: String,
    val deskripsi: String,
    val alamat: String,
    val jam_buka: String? = null,
    val harga: String? = null,
    val kontak: String? = null,
    val created_at: String,
    val gambar: String? = null
)

data class Destination(
    val id: Long,
    val name: String,
    val description: String,
    val address: String,
    val openHours: String?,
    val price: String?,
    val contact: String?,
    val createdAt: Instant,
    val imageUrls: String?,
)