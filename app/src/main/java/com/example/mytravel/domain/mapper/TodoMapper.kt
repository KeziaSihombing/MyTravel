package com.example.mytravel.domain.mapper;

import com.example.mytravel.domain.model.Comment;
import com.example.mytravel.domain.model.CommentDto;
import java.time.Instant;

object CommentMapper {
    fun map(dto: CommentDto): Comment =
        Comment(
            id = dto.id,
            userId = dto.user_id,
            reviewId = dto.review_id,
            komentar = dto.komentar,
            likes = dto.likes,
            gambar = dto.gambar ?: emptyList(),
            createdAt = Instant.parse(dto.created_at),
            updatedAt = Instant.parse(dto.updated_at)
        )

    fun mapList(dtos: List<CommentDto>): List<Comment> = dtos.map { map(it) }
}
