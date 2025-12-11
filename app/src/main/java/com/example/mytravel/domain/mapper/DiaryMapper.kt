package com.example.mytravel.domain.mapper




import com.example.mytravel.domain.model.DiaryEntry
import com.example.mytravel.domain.model.DiaryEntryDto
import java.time.Instant




object DiaryMapper {
    fun map(dto: DiaryEntryDto): DiaryEntry {
        return DiaryEntry(
            id = dto.id,
            title = dto.title,
            content = dto.content,
            imageUrl = dto.image_url,
            color = dto.color,
            createdAt = Instant.parse(dto.created_at),
            updatedAt = dto.updated_at?.let { Instant.parse(it) }
        )
    }




    fun mapList(dtoList: List<DiaryEntryDto>): List<DiaryEntry> {
        return dtoList.map { map(it) }
    }
}