package com.example.mytravel.domain.mapper

import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.model.Profile;
import com.example.mytravel.domain.model.ProfileDto;
import java.time.Instant;

object ProfileMapper {
    fun map(dto: ProfileDto): Profile {
        val email = SupabaseHolder.session()?.user?.email ?: ""
        return Profile(
            id = dto.id,
            name = dto.name,
            email = email,
            description = dto.description,
            createdAt = Instant.parse(dto.created_at),
            updatedAt = Instant.parse(dto.updated_at)
        )
    }
}

