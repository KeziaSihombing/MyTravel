package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import com.example.mytravel.domain.mapper.ReviewMapper
import com.example.mytravel.domain.model.Review
import com.example.mytravel.domain.model.ReviewDto
import com.example.mytravel.domain.model.ReviewInsertDto
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ReviewRepository(
    private val profileRepo: ProfileRepository = ProfileRepository()
) {
    private val postgrest get() = SupabaseHolder.client.postgrest
    private val storage get() = SupabaseHolder.client.storage.from("review-images")

    private fun resolveImageUrl(path: String?): String? {
        if (path == null) return null
        return storage.publicUrl(path)
    }

    suspend fun fetchReviews(destinationId: Long): List<Review> {
        val response = postgrest["review"].select {
            filter { eq("wisata_id", destinationId) }
        }

        val list = response.decodeList<ReviewDto>()

        return list.map { dto ->
            val profile = profileRepo.fetchUserByID(dto.user_id)

            ReviewMapper.map(
                dto,
                profile?.name ?: "Anonymous",
                ::resolveImageUrl
            )
        }
    }

    suspend fun getReview(id: Long): Review? {
        val response = postgrest["review"].select {
            filter { eq("id", id) }
        }
        Log.d("GET_DESTINATION", "raw=" + (response.data ?: "null"))

        val dto = response.decodeList<ReviewDto>().firstOrNull() ?: return null
        val profile = profileRepo.fetchUserByID(dto.user_id)

        return ReviewMapper.map(
            dto,
            profile?.name ?: "Anonymous",
            ::resolveImageUrl
        )
    }

    private suspend fun uploadImage(file: File, uid: String): String = withContext(Dispatchers.IO) {
        val objectName = "$uid/${UUID.randomUUID()}_${file.name}"
        storage.upload(objectName, file.readBytes())
        objectName
    }

    suspend fun addReview(
        destinationId: Long,
        content: String,
        imageFile: File?
    ): Review {

        val uid = SupabaseHolder.session()?.user?.id
            ?: throw IllegalStateException("Not logged in")

        var imagePath: String? = null
        if (imageFile != null) {
            imagePath = uploadImage(imageFile, uid)
        }

        val insert = postgrest["review"].insert(
            ReviewInsertDto(
                userId = uid,
                wisataId = destinationId,
                review = content,
                gambar = imagePath
            )
        ) { select() }

        val dto = insert.decodeSingle<ReviewDto>()
        val profile = profileRepo.fetchUserByID(dto.user_id)
        return ReviewMapper.map(
            dto,
            profile?.name ?: "Anonymous",
            ::resolveImageUrl
        )
    }
}
