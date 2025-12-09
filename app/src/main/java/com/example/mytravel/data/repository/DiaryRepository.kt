package com.example.mytravel.data.repository

import com.example.mytravel.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.result.decodeList
import io.github.jan.supabase.postgrest.result.decodeSingle
import io.github.jan.supabase.storage.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DiaryRepository {
    private val supabase = SupabaseClient.client
    private val bucketName = "diary-images"

    suspend fun getAllDiaries(): List<DiaryEntry> {
        return try {
            supabase.postgrest["diaries"] // Ganti .from() dengan .postgrest[]
                .select()
                .decodeList<DiaryEntry>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getDiaryById(id: Int): DiaryEntry? {
        return try {
            supabase.postgrest["diaries"] // Ganti .from() dengan .postgrest[]
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

    suspend fun createDiary(entry: DiaryEntry): Boolean {
        return try {
            supabase.postgrest["diaries"].insert(entry) // Ganti .from() dengan .postgrest[]
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateDiary(entry: DiaryEntry): Boolean {
        return try {
            supabase.postgrest["diaries"].update(entry) { // Ganti .from() dengan .postgrest[]
                filter {
                    eq("id", entry.id!!)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteDiary(id: Int): Boolean {
        return try {
            supabase.postgrest["diaries"].delete { // Ganti .from() dengan .postgrest[]
                filter {
                    eq("id", id)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun uploadImage(fileBytes: ByteArray, fileName: String): String? {
        return try {
            val uniqueFileName = "${UUID.randomUUID()}_$fileName"
            val bucket = supabase.storage[bucketName] // Ganti .storage.from() dengan .storage[]
            bucket.upload(uniqueFileName, fileBytes)
            bucket.publicUrl(uniqueFileName)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteImage(imageUrl: String): Boolean {
        return try {
            val fileName = imageUrl.substringAfterLast("/")
            supabase.storage[bucketName].delete(listOf(fileName)) // Ganti .storage.from() dan bungkus dengan listOf
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
