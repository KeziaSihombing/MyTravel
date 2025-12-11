package com.example.mytravel.domain.mapper


import com.example.mytravel.domain.model.DiaryEntry
import com.example.mytravel.domain.model.DiaryEntryDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder


object DiaryMapper {

    private val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")       // bagian wajib
        .optionalStart()
        .appendPattern(".SSSSSS")                // optional microseconds
        .optionalEnd()
        .optionalStart()
        .appendPattern("XXX")                    // optional timezone offset (+00:00)
        .optionalEnd()
        .toFormatter()

    fun map(dto: DiaryEntryDto): DiaryEntry {

        val createdAtInstant =
            dto.created_at?.let {
                LocalDateTime.parse(it, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            } ?: Instant.now()

        val updatedAtInstant =
            dto.updated_at?.let {
                LocalDateTime.parse(it, formatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            }

        return DiaryEntry(
            id = dto.id,
            title = dto.title,
            content = dto.content,
            imageUrl = dto.image_url,
            color = dto.color,
            createdAt = createdAtInstant,
            updatedAt = updatedAtInstant
        )
    }

    fun mapList(dtoList: List<DiaryEntryDto>): List<DiaryEntry> {
        return dtoList.map { map(it) }
    }
}
