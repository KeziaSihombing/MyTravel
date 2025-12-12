package com.example.mytravel.data.repository

import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.model.Budget
import com.example.mytravel.domain.model.Rencana 
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import java.io.File

class BudgetRepository {

    suspend fun fetchAllRencana(): List<Rencana> {
        return SupabaseHolder.client.postgrest["rencana"].select().decodeList()
    }

    suspend fun fetchAllBudgets(): List<Budget> {
        return SupabaseHolder.client.postgrest["budget"].select().decodeList()
    }

    suspend fun getBudgetById(budgetId: Long): Budget? {
        return SupabaseHolder.client.postgrest["budget"].select {
            filter {
                eq("id", budgetId)
            }
        }.decodeSingleOrNull()
    }

    suspend fun fetchBudgetsForRencana(rencanaId: Long): List<Budget> {
        return SupabaseHolder.client.postgrest["budget"].select {
            filter {
                eq("rencana_id", rencanaId)
            }
        }.decodeList<Budget>()
    }

    fun listenToBudgetChanges(rencanaId: Long): Flow<List<Budget>> {
        return SupabaseHolder.client.realtime
            .channel("budget-rencana-$rencanaId")
            .postgresChangeFlow<PostgresAction>(schema = "public") { 
                table = "budget"
                filter = "rencana_id=eq.$rencanaId"
            }.mapLatest { 
                fetchBudgetsForRencana(rencanaId)
            }.onStart { 
                emit(fetchBudgetsForRencana(rencanaId)) 
            }
    }

    fun listenToAnyBudgetChange(): Flow<Unit> {
        return SupabaseHolder.client.realtime
            .channel("public:budget")
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "budget"
            }
            .map { }
            .onStart { emit(Unit) } 
    }

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
            rencanaId = rencanaId, 
            title = title,
            nominal = nominal,
            imageUrl = imageUrl
        )

        return SupabaseHolder.client.postgrest["budget"].insert(newBudget) { select() }.decodeSingle<Budget>()
    }
    
    suspend fun updateBudget(budgetId: Long, title: String, nominal: Double) {
        SupabaseHolder.client.postgrest["budget"].update({
            set("title", title)
            set("nominal", nominal)
        }) {
            filter {
                eq("id", budgetId)
            }
        }
    }

    suspend fun deleteBudget(budgetId: Long) {
        SupabaseHolder.client.postgrest["budget"].delete {
            filter {
                eq("id", budgetId)
            }
        }
    }
}