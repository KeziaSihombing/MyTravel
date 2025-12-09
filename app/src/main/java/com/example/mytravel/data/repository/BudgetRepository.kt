package com.example.mytravel.data.repository

import com.example.mytravel.data.model.BudgetItem
import com.example.mytravel.data.remote.SupabaseHolder
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json

class BudgetRepository {

    suspend fun getBudgetItems(userId: String): List<BudgetItem> {
        return try {
            val result = SupabaseHolder.client.postgrest["budget"].select {
                filter {
                    eq("user_id", userId)
                }
            }
            // Dekode JSON secara manual untuk menghindari masalah cache IDE
            Json.decodeFromString<List<BudgetItem>>(result.data)
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
        // 1. Upload the file with its path (fileName)
        bucket.upload(path = fileName, data = imageBytes, upsert = true)
        // 2. Get the public URL for that same path
        return bucket.publicUrl(fileName)
    }
}
