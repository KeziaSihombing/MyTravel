package com.example.mytravel.data.repository




import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.mapper.CommentMapper
import com.example.mytravel.domain.mapper.DiaryMapper
import com.example.mytravel.domain.model.CommentDto
import com.example.mytravel.domain.model.DiaryEntry
import com.example.mytravel.domain.model.DiaryEntryDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.time.Instant




class DiaryRepository {
    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("diary-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        return storage.publicUrl(path) // atau signed URL kalau private
    }

    suspend fun uploadImage(bytes: ByteArray, uid: String): String {
        val fileName = "$uid/${UUID.randomUUID()}.jpg"
        storage.upload(fileName, bytes)
        return fileName // simpan path-nya saja
    }

    suspend fun createDiary(entry: DiaryEntry): Boolean {
        val userId = SupabaseHolder.session()?.user?.id ?: return false

        val map = mapOf(
            "user_id" to userId,
            "title" to entry.title,
            "content" to entry.content,
            "image_url" to entry.imageUrl,
            "color" to entry.color,
            "created_at" to entry.createdAt
        )

        postgrest["diary_entries"].insert(map)
        return true
    }

    suspend fun getAllDiaries(): List<DiaryEntry> {
        val response = postgrest["diary_entries"].select {
            order("created_at", Order.DESCENDING)
        }
        Log.d("GET_ALL_DIARIES", "raw=" + (response.data ?: "null"))
        val list = response.decodeList<DiaryEntryDto>()
        return DiaryMapper.mapList(list)
    }


    suspend fun getDiaryById(id: Int): DiaryEntry? {
        return try {
            postgrest["diaries"]
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<DiaryEntry>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteDiary(id: Int): Boolean {
        return try {
            postgrest["diaries"]
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }


    fun getCurrentTimestamp(): Instant {
        return Instant.now()
    }
}