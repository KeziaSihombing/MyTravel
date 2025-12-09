package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseClient
import com.example.mytravel.domain.mapper.CommentMapper
import com.example.mytravel.domain.model.Comment
import com.example.mytravel.domain.model.CommentDto
import io.github.jan.supabase.postgrest.query.Order
import com.example.mytravel.domain.model.CommentWithUserName
import io.github.jan.supabase.postgrest.postgrest

class CommentRepository {
    private val postgrest get() = SupabaseClient.client.postgrest

    suspend fun getCommentsByReviewID(reviewID: Long): List<Comment>{
        val response = postgrest["review"].select {
            filter {
                eq("id", reviewID)
            }
            order("created_at", Order.DESCENDING)
        }
        val list = response.decodeList<CommentDto>()
        return list.map{CommentMapper.map(it)}
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
                likes = comment.likes,
                gambar = comment.gambar,
                createdAt = comment.createdAt,
                updatedAt = comment.updatedAt
            )
        }
    }



}
