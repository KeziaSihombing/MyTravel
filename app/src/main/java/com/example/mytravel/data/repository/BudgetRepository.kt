package com.example.mytravel.data.repository

import com.example.mytravel.data.model.BudgetItem
import com.example.mytravel.data.remote.SupabaseHolder
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json

class BudgetRepository {

    // Buat instance Json untuk parsing, ignoreUnknownKeys penting untuk stabilitas
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getBudgetItems(userId: String): List<BudgetItem> {
        return try {
            // 1. Dapatkan hasil mentah dari Supabase
            val result = SupabaseHolder.client.postgrest["budget"].select {
                filter {
                    eq("user_id", userId)
                }
            }
            // 2. Dekode String JSON secara manual
            json.decodeFromString<List<BudgetItem>>(result.data)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun addBudgetItem(budgetItem: BudgetItem) {
        SupabaseHolder.client.postgrest["budget"].insert(budgetItem)
    }

    suspend fun uploadBudgetImage(imageBytes: ByteArray, userId: String): String {
        val fileName = "$userId/${System.currentTimeMillis()}.jpg"
        val bucket = SupabaseHolder.client.storage["budget_images"]
        // Upload file
        bucket.upload(path = fileName, data = imageBytes, upsert = true)
        // Dapatkan URL publiknya
        return bucket.publicUrl(fileName)
    }
}
