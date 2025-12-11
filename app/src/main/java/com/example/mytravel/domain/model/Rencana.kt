package com.example.mytravel.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rencana(
    @SerialName("id")
    val id: Long = 0,

    @SerialName("user_id")
    val userId: String,

    @SerialName("wisata_id")
    val wisataId: Long,

    @SerialName("judul")
    val judul: String,

    @SerialName("deskripsi")
    val deskripsi: String,

    @SerialName("gambar")
    val gambar: String? = null,

    // Kolom ini tidak ada di skema DB Anda, tapi ada di desain.
    // Anda mungkin perlu menambahkannya ke tabel `rencana` dengan tipe `text` atau `date`.
    @SerialName("target_date")
    val targetDate: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)
