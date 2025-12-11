package com.example.mytravel.data.repository

import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.model.Rencana
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.io.File

class PlanRepository {

    suspend fun fetchAllPlans(): List<Rencana> {
        val userId = SupabaseHolder.client.auth.currentUserOrNull()?.id ?: return emptyList()
        return SupabaseHolder.client.postgrest["rencana"].select {
            filter {
                eq("user_id", userId)
            }
        }.decodeList()
    }

    suspend fun fetchPlanById(planId: Long): Rencana? {
        return SupabaseHolder.client.postgrest["rencana"].select {
            filter {
                eq("id", planId)
            }
        }.decodeSingleOrNull()
    }

    suspend fun addPlan(rencana: Rencana, imageFile: File?): Rencana {
        var imageUrl: String? = null
        if (imageFile != null) {
            val filePath = "${rencana.userId}/${System.currentTimeMillis()}_plan_image.jpg"
            // Assuming you have a 'plan_images' bucket in Supabase Storage
            SupabaseHolder.client.storage["plan_images"].upload(filePath, imageFile.readBytes())
            imageUrl = SupabaseHolder.client.storage["plan_images"].publicUrl(filePath)
        }

        val newRencana = rencana.copy(gambar = imageUrl)

        return SupabaseHolder.client.postgrest["rencana"].insert(newRencana) { select() }.decodeSingle()
    }
}
