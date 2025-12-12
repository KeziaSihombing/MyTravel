package com.example.mytravel.domain.mapper

import com.example.mytravel.domain.model.Plan
import com.example.mytravel.domain.model.PlanDto
import kotlinx.datetime.Instant

object PlanMapper {
    fun map(dto: PlanDto, imageUrlResolver: (String?) -> String?): Plan =
        Plan(
            id = dto.id,
            userId = dto.user_id,
            destinationId = dto.wisata_id,
            title = dto.judul,
            content = dto.deskripsi,
            images = imageUrlResolver(dto.gambar),
            createdAt = Instant.parse(dto.created_at),
        )
}