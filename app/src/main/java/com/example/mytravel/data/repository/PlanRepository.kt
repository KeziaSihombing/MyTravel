package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.mapper.PlanMapper
import com.example.mytravel.domain.model.Plan
import com.example.mytravel.domain.model.PlanDto
import com.example.mytravel.domain.model.PlanInsertDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import java.io.File

class PlanRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("plan-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path.isNullOrBlank()) {
            Log.d("PLAN_REPO", "resolveImageUrl: path null/blank")
            return null
        }
        return try {
            val url = storage.publicUrl(path)
            Log.d("PLAN_REPO", "resolveImageUrl: path=$path, url=$url")
            url
        } catch (e: Exception) {
            Log.e("PLAN_REPO", "Error resolving image URL: ${e.message}")
            null
        }
    }

    suspend fun fetchPlans(): List<Plan> {
        return try {
            val userId = SupabaseHolder.session()?.user?.id
            if (userId == null) {
                Log.e("PLAN_REPO", "User not logged in")
                return emptyList()
            }

            Log.d("PLAN_REPO", "Fetching plans for user: $userId")

            val response = postgrest["rencana"].select() {
                filter {
                    eq("user_id", userId)
                }
                order("created_at", Order.DESCENDING)
            }

            val list = response.decodeList<PlanDto>()
            Log.d("PLAN_REPO", "Fetched ${list.size} plans from database")

            list.forEachIndexed { index, dto ->
                Log.d("PLAN_REPO", "Plan[$index]: id=${dto.id}, judul=${dto.judul}, gambar=${dto.gambar}")
            }

            val plans = list.map { dto ->
                val plan = PlanMapper.map(dto, ::resolveImageUrl)
                Log.d("PLAN_REPO", "Mapped plan: id=${plan.id}, images=${plan.images}")
                plan
            }

            plans
        } catch (e: Exception) {
            Log.e("PLAN_REPO", "Error fetching plans: ${e.message}", e)
            emptyList()
        }
    }

    // Mengupload image ke Supabase
    private suspend fun uploadImage(imageFile: File, userId: String): String? {
        return try {
            val fileName = "$userId/${System.currentTimeMillis()}.jpg"
            val path = "$fileName"

            Log.d("PLAN_REPO", "Uploading image: $path")
            Log.d("PLAN_REPO", "File exists: ${imageFile.exists()}, size: ${imageFile.length()} bytes")

            storage.upload(
                path = path,
                data = imageFile.readBytes(),
                upsert = false
            )

            Log.d("PLAN_REPO", "Image uploaded successfully: $path")

            path
        } catch (e: Exception) {
            Log.e("PLAN_REPO", "Error uploading image: ${e.message}", e)
            null
        }
    }

    // Menambahkan plan baru
    suspend fun addPlan(
        destinationId: Long,
        title: String,
        content: String,
        imageFile: File?
    ): Plan {
        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("Not logged in")

        Log.d("PLAN_REPO", "Adding plan for destination: $destinationId")
        Log.d("PLAN_REPO", "Title: $title")
        Log.d("PLAN_REPO", "Has image: ${imageFile != null}")

        // Upload image
        var imagePath: String? = null
        if (imageFile != null && imageFile.exists()) {
            Log.d("PLAN_REPO", "Uploading image file: ${imageFile.absolutePath}")
            imagePath = uploadImage(imageFile, userId)
            Log.d("PLAN_REPO", "Image path after upload: $imagePath")
        } else {
            Log.d("PLAN_REPO", "No image to upload or file doesn't exist")
        }

        // Insert ke database
        val insertDto = PlanInsertDto(
            user_id = userId,
            wisata_id = destinationId,
            judul = title,
            deskripsi = content,
            gambar = imagePath
        )

        Log.d("PLAN_REPO", "Inserting plan with gambar: $imagePath")

        val insert = postgrest["rencana"].insert(insertDto) {
            select()
        }

        val dto = insert.decodeSingle<PlanDto>()
        Log.d("PLAN_REPO", "Plan inserted with id: ${dto.id}, gambar: ${dto.gambar}")

        val plan = PlanMapper.map(dto, ::resolveImageUrl)
        Log.d("PLAN_REPO", "Final plan images URL: ${plan.images}")

        return plan
    }

    // Mengambil rencana berdasarkan ID
    suspend fun getPlan(id: Long): Plan? {
        return try {
            val userId = SupabaseHolder.session()?.user?.id
            if (userId == null) {
                Log.e("PLAN_REPO", "User not logged in")
                return null
            }

            Log.d("PLAN_REPO", "Fetching plan with id: $id")

            val response = postgrest["rencana"].select {
                filter {
                    eq("id", id)
                    eq("user_id", userId)
                }
            }

            Log.d("PLAN_REPO", "Raw response: ${response.data}")

            val dto = response.decodeList<PlanDto>().firstOrNull()
            if (dto == null) {
                Log.e("PLAN_REPO", "Plan not found: $id")
                return null
            }

            Log.d("PLAN_REPO", "Found plan: id=${dto.id}, gambar=${dto.gambar}")

            val plan = PlanMapper.map(dto, ::resolveImageUrl)
            Log.d("PLAN_REPO", "Mapped plan images URL: ${plan.images}")

            plan
        } catch (e: Exception) {
            Log.e("PLAN_REPO", "Error fetching plan: ${e.message}", e)
            null
        }
    }
}