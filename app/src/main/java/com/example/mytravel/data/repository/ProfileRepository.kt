package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.mapper.ProfileMapper
import com.example.mytravel.domain.model.Profile
import com.example.mytravel.domain.model.ProfileDto
import io.github.jan.supabase.postgrest.postgrest


class ProfileRepository {
    private val postgrest get() = SupabaseHolder.client.postgrest

    suspend fun fetchProfile(): Profile? {
        val userId = SupabaseHolder.session()?.user?.id ?: return null
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

    suspend fun editProfile(id: String, newName: String, newDescription: String): Profile? {
        Log.d("EDIT_PROFILE", "Updating id=$id name=$newName description=$newDescription")
        val update = postgrest["akun"].update(
            mapOf(
                "name" to newName,
                "description" to newDescription
            )
        ) {
            filter { eq("id", id) }
            select()
        }
        val list = update.decodeList<ProfileDto>()
        Log.d("EDIT_PROFILE_RAW", update.data.toString())
        if (list.isEmpty()) {
            Log.e("EDIT_PROFILE", "Update succeeded but returned NO ROWS")
            return null
        }
        val dto = list.first()
        Log.d("EDIT_PROFILE_DTO", dto.toString())

        return ProfileMapper.map(dto)
    }



}