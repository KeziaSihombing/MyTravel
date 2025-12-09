package com.example.mytravel.data.repository

import com.example.mytravel.data.remote.SupabaseClient
import com.example.mytravel.domain.model.DiaryEntry
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DiaryRepository {
    private val supabase = SupabaseClient.client
    private val bucketName = "diary-images"
    // Buat instance Json untuk parsing, ignoreUnknownKeys penting untuk stabilitas
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getAllDiaries(): List<DiaryEntry> {
        return try {
            val result = supabase.postgrest["diaries"].select()
            // Dekode JSON secara manual untuk menghindari masalah cache IDE
            json.decodeFromString<List<DiaryEntry>>(result.data)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getDiaryById(id: Int): DiaryEntry? {
        return try {
            val result = supabase.postgrest["diaries"].select {
                filter {
                    eq("id", id)
                }
            }
            // Dekode JSON secara manual untuk menghindari masalah cache IDE
            json.decodeFromString<List<DiaryEntry>>(result.data).firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createDiary(entry: DiaryEntry): Boolean {
        return try {
            supabase.postgrest["diaries"].insert(entry)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateDiary(entry: DiaryEntry): Boolean {
        return try {
            supabase.postgrest["diaries"].update(entry) {
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
            supabase.postgrest["diaries"].delete {
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
            val bucket = supabase.storage[bucketName]
            bucket.upload(uniqueFileName, fileBytes)
            bucket.publicUrl(uniqueFileName)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteImage(imageUrl: String): Boolean {
        return try {
            val fileName = imageUrl.substringAfterLast("/")
            supabase.storage[bucketName].delete(listOf(fileName))
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
