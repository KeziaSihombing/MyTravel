package com.example.mytravel.domain.mapper

import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.model.DiaryEntry
import com.example.mytravel.domain.model.DiaryEntryDto
import io.github.jan.supabase.storage.storage
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatterBuilder

object DiaryMapper {

    private val formatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .optionalStart()
        .appendPattern(".SSSSSS")
        .optionalEnd()
        .optionalStart()
        .appendPattern("XXX")
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
            imageUrl = dto.image_url?.let { path ->
                SupabaseHolder.client
                    .storage
                    .from("diary-images")
                    .publicUrl(path)   // 🔥 INI YANG PENTING
            },
            color = dto.color,
            createdAt = createdAtInstant,
            updatedAt = updatedAtInstant
        )
    }

    fun mapList(dtoList: List<DiaryEntryDto>): List<DiaryEntry> {
        return dtoList.map { map(it) }
    }
}
