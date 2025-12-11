package com.example.mytravel.data.repository

import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.model.Budget
import com.example.mytravel.domain.model.Rencana // Pastikan Rencana di-import
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.gotrue.auth
import java.io.File

class BudgetRepository {

    // Fungsi baru untuk mengambil semua Rencana
    suspend fun fetchAllRencana(): List<Rencana> {
        return SupabaseHolder.client.postgrest["rencana"].select().decodeList()
    }

    // Mengambil budget items untuk rencana tertentu
    suspend fun fetchBudgetsForRencana(rencanaId: Long): List<Budget> {
        return SupabaseHolder.client.postgrest["budget"].select {
            filter {
                eq("rencana_id", rencanaId)
            }
        }.decodeList<Budget>()
    }

    // Menambahkan budget item baru ke sebuah rencana
    suspend fun addBudgetToRencana(rencanaId: Long, title: String, nominal: Double, attachment: File?): Budget {
        val userId = SupabaseHolder.client.auth.currentUserOrNull()?.id
        require(userId != null) { "User not logged in" }

        var imageUrl: String? = null
        if (attachment != null) {
            val filePath = "$userId/${System.currentTimeMillis()}_${attachment.name}"
            SupabaseHolder.client.storage["budget_images"].upload(filePath, attachment.readBytes())
            imageUrl = SupabaseHolder.client.storage["budget_images"].publicUrl(filePath)
        }

        val newBudget = Budget(
            userId = userId,
            rencanaId = rencanaId, // Menyimpan ID rencana
            title = title,
            nominal = nominal,
            imageUrl = imageUrl
        )

        return SupabaseHolder.client.postgrest["budget"].insert(newBudget) { select() }.decodeSingle<Budget>()
    }
}
