package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.mapper.DestinationMapper
import com.example.mytravel.domain.mapper.ReviewMapper
import com.example.mytravel.domain.model.Destination
import com.example.mytravel.domain.model.DestinationDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

class DestinationRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("destinations")
    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        return storage.publicUrl(path)
    }

    suspend fun fetchDestinations(): List<Destination> {
        val response = postgrest["tempat_wisata"].select()
        val list = response.decodeList<DestinationDto>()
        return list.map { dto ->

            DestinationMapper.map(
                dto,
                ::resolveImageUrl
            )
        }
    }

    suspend fun getDestination(id: Long): Destination? {
        val response = postgrest["tempat_wisata"].select {
            filter { eq("id", id) }
        }
        Log.d("GET_DESTINATION", "raw=" + (response.data ?: "null"))
        val dto = response.decodeList<DestinationDto>().firstOrNull() ?: return null
        return DestinationMapper.map(dto, ::resolveImageUrl)
    }
}