package com.example.mytravel.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rencana(
    @SerialName("id")
    val id: Long,
    @SerialName("judul")
    val judul: String
    // Anda bisa menambahkan kolom lain dari tabel `rencana` jika dibutuhkan di sini
)
