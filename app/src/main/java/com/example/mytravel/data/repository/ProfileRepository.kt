package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseClient
import com.example.mytravel.domain.mapper.ProfileMapper
import com.example.mytravel.domain.model.Profile
import com.example.mytravel.domain.model.ProfileDto
import io.github.jan.supabase.postgrest.postgrest


class ProfileRepository {
    private val postgrest get() = SupabaseClient.client.postgrest

    suspend fun fetchProfile(): Profile? {
        val userId = SupabaseClient.session()?.user?.id ?: return null
        val response = postgrest["akun"].select {
            filter {
                eq("id", userId)
            }
        }
        Log.d("GET_PROFILE", "raw=" + (response.data ?: "null"))
        val profileDto = response.decodeList<ProfileDto>().firstOrNull()?: return null
        return ProfileMapper.map(profileDto)
    }

    suspend fun fetchUserByID(id: String) : Profile? {
        val response = postgrest["akun"].select {
            filter {
                eq("id", id)
            }
        }
        Log.d("GET_PROFILE", "raw=" + (response.data ?: "null"))
        val userDto = response.decodeList<ProfileDto>().firstOrNull()?: return null
        return ProfileMapper.map(userDto)
    }

}