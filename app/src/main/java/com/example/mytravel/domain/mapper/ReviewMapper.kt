package com.example.mytravel.domain.mapper

import com.example.mytravel.domain.model.Review
import com.example.mytravel.domain.model.ReviewDto
import kotlinx.datetime.Instant

object ReviewMapper {
    fun map(dto: ReviewDto, userName: String, imageUrlResolver: (String?) -> String?): Review =

        Review(
            id = dto.id,
            userId = dto.user_id,
            userName = userName,
            destinationId = dto.wisata_id,
            content = dto.review,
            images = imageUrlResolver(dto.gambar),
            createdAt = Instant.parse(dto.created_at),
            updatedAt = Instant.parse(dto.updated_at)
        )
}