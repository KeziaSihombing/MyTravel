package com.example.mytravel.domain.mapper;

import com.example.mytravel.domain.model.Comment;
import com.example.mytravel.domain.model.CommentDto;
import java.time.Instant;
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder

object CommentMapper {
    private val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart().appendPattern(".SSSSSS").optionalEnd()
        .toFormatter()

    fun map(dto: CommentDto): Comment {
        val createdAt = LocalDateTime.parse(dto.created_at, formatter)
            .atZone(ZoneId.systemDefault()) // WIB or device timezone
            .toInstant()

        val updatedAt = LocalDateTime.parse(dto.updated_at, formatter)
            .atZone(ZoneId.systemDefault()) // WIB or device timezone
            .toInstant()


        return Comment(
            id = dto.id,
            userId = dto.user_id,
            reviewId = dto.review_id,
            komentar = dto.komentar,
            gambar = dto.gambar ?: emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun mapList(dtos: List<CommentDto>): List<Comment> = dtos.map { map(it) }
}
