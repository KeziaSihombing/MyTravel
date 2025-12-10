//package com.example.mytravel.data.repository
//
//import android.net.Uri
//import com.example.mytravel.data.remote.SupabaseClient
//import io.github.jan.supabase.postgrest.from
//import io.github.jan.supabase.storage.storage
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import java.util.UUID
//
//class DiaryRepository {
//    private val supabase = SupabaseClient.client
//    private val bucketName = "diary-images"
//
//    suspend fun getAllDiaries(): List<DiaryEntry> {
//        return try {
//            supabase.from("diaries")
//                .select()
//                .decodeList<DiaryEntry>()
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    suspend fun getDiaryById(id: Int): DiaryEntry? {
//        return try {
//            supabase.from("diaries")
//                .select {
//                    filter {
//                        eq("id", id)
//                    }
//                }
//                .decodeSingle<DiaryEntry>()
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun createDiary(entry: DiaryEntry): Boolean {
//        return try {
//            supabase.from("diaries")
//                .insert(entry)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    suspend fun updateDiary(entry: DiaryEntry): Boolean {
//        return try {
//            supabase.from("diaries")
//                .update(entry) {
//                    filter {
//                        eq("id", entry.id!!)
//                    }
//                }
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    suspend fun deleteDiary(id: Int): Boolean {
//        return try {
//            supabase.from("diaries")
//                .delete {
//                    filter {
//                        eq("id", id)
//                    }
//                }
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    suspend fun uploadImage(fileBytes: ByteArray, fileName: String): String? {
//        return try {
//            val uniqueFileName = "${UUID.randomUUID()}_$fileName"
//            supabase.storage.from(bucketName).upload(uniqueFileName, fileBytes)
//            supabase.storage.from(bucketName).publicUrl(uniqueFileName)
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun deleteImage(imageUrl: String): Boolean {
//        return try {
//            val fileName = imageUrl.substringAfterLast("/")
//            supabase.storage.from(bucketName).delete(fileName)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    fun getCurrentTimestamp(): String {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//        return dateFormat.format(Date())
//    }
//}