package com.example.mytravel.domain.mapper

import com.example.mytravel.domain.model.Destination
import com.example.mytravel.domain.model.DestinationDto
import java.time.Instant

object DestinationMapper {
    fun map(
        dto: DestinationDto, imageUrlResolver: (String?) -> String?
    ): Destination {

        val image = imageUrlResolver(dto.gambar)
        return Destination(
            id = dto.id,
            name = dto.nama_wisata,
            description = dto.deskripsi,
            address = dto.alamat,
            openHours = dto.jam_buka,
            price = dto.harga,
            contact = dto.kontak,
            createdAt = Instant.parse(dto.created_at),
            imageUrls = image
        )
    }
}