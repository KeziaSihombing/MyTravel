package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.ReviewRepository
import com.example.mytravel.domain.model.Review
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ReviewViewModel(
    private val repo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _reviews = MutableStateFlow<UiResult<List<Review>>>(UiResult.Loading)
    val reviews: StateFlow<UiResult<List<Review>>> = _reviews

    private val _adding = MutableStateFlow<UiResult<Review>?>(null)
    val adding: StateFlow<UiResult<Review>?> = _adding

    private val _reviewDetail = MutableStateFlow<UiResult<Review>?>(null)
    val reviewDetail: StateFlow<UiResult<Review>?> = _reviewDetail

    fun loadReviews(destinationId: Long) {
        _reviews.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val list = repo.fetchReviews(destinationId)
                _reviews.value = UiResult.Success(list)
            } catch (e: Exception) {
                _reviews.value = UiResult.Error(e.message ?: "Gagal memuat review")
            }
        }
    }

    fun addReview(
        destinationId: Long,
        content: String,
        imageFile: File?
    ) {
        _adding.value = UiResult.Loading

        viewModelScope.launch {
            try {
                val review = repo.addReview(destinationId, content, imageFile)
                _adding.value = UiResult.Success(review)
                loadReviews(destinationId)
            } catch (e: Exception) {
                _adding.value = UiResult.Error(e.message ?: "Gagal menambah review")
            }
        }
    }

    suspend fun getReview(id: Long): Review? = repo.getReview(id)

    fun loadReviewDetail(id: Long) {
        _reviewDetail.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val result = repo.getReview(id)
                if (result != null) {
                    _reviewDetail.value = UiResult.Success(result)
                } else {
                    _reviewDetail.value = UiResult.Error("Review tidak ditemukan")
                }
            } catch (e: Exception) {
                _reviewDetail.value = UiResult.Error(e.message ?: "Gagal memuat detail review")
            }
        }
    }
}
