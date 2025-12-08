package com.example.mytravel.data.repository

import com.example.mytravel.data.model.BudgetItem
import com.example.mytravel.data.remote.SupabaseHolder
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.from
import io.github.jan.supabase.storage.storage
import io.ktor.client.call.body

class BudgetRepository {

    suspend fun getBudgetItems(userId: String): List<BudgetItem> {
        return try {
            SupabaseHolder.client.postgrest["budget"].select {
                filter {
                    eq("user_id", userId)
                }
            }.body()
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
        val path = bucket.upload(path = fileName, data = imageBytes, upsert = true)
        return SupabaseHolder.client.storage.from("budget_images").publicUrl(path)
    }
}
