package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.mapper.CommentMapper
import com.example.mytravel.domain.model.Comment
import com.example.mytravel.domain.model.CommentDto
import com.example.mytravel.domain.model.CommentWithUserName
import com.example.mytravel.domain.model.NewCommentRequest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class CommentRepository {

    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("comment-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        // Bila bucket private gunakan signed URL:
        return storage.publicUrl(path)
    }

    suspend fun getCommentsByReviewID(reviewID: Long): List<Comment> {
        val response = postgrest["komentar"].select {
            filter { eq("review_id", reviewID)
        }
            order("created_at", Order.DESCENDING)
        }
        Log.d("GET_CommentsByID", "raw=" + (response.data ?: "null"))
        val list = response.decodeList<CommentDto>()
        return list.map { CommentMapper.map(it, ::resolveImageUrl) }
    }

    suspend fun getCommentsWithUserName(reviewID: Long): List<CommentWithUserName> {
        val comments = getCommentsByReviewID(reviewID)
        return comments.map { comment ->
            val profile = ProfileRepository().fetchUserByID(comment.userId)
            CommentWithUserName(
                id = comment.id,
                userId = comment.userId,
                userName = profile?.name ?: "Unknown",
                reviewId = comment.reviewId,
                komentar = comment.komentar,
                gambar = comment.gambar,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }
    }

    suspend fun addComment(
        reviewID: Long,
        komentar: String?,
        imageFiles: List<File> = emptyList()
    ): Comment {
        val userId = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("User not logged in")

        // Upload semua gambar dulu
        val imagePaths: List<String> = imageFiles.map { file -> uploadImage(file, userId) }

        val request = NewCommentRequest(
            user_id = userId,
            review_id = reviewID,
            komentar = komentar?: "",
            gambar = imagePaths
        )

        val insert = postgrest["komentar"].insert(request) {
            select()
        }
        val dto = insert.decodeSingle<CommentDto>()
        return CommentMapper.map(dto, ::resolveImageUrl)
    }

    private suspend fun uploadImage(file: File, uid: String): String = withContext(Dispatchers.IO) {
        val objectName = "$uid/${UUID.randomUUID()}_${file.name}"

        storage.upload(
            path = objectName,
            data = file.readBytes(),
            upsert = true
        )

        // Kembalikan URL yang bisa langsung dipakai di Image()
        storage.publicUrl(objectName)
    }
}
