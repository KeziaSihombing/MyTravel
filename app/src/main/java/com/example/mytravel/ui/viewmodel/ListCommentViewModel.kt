package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.CommentRepository
import com.example.mytravel.data.repository.ProfileRepository
import com.example.mytravel.domain.model.CommentWithUserName
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ListCommentViewModel(
    private val commentRepo : CommentRepository = CommentRepository()
): ViewModel() {
    private val _comments = MutableStateFlow<UiResult<List<CommentWithUserName>>>(UiResult.Loading)
    val comments: StateFlow<UiResult<List<CommentWithUserName>>> = _comments

    fun getCommentsWithUserName(reviewID: Long) {
        _comments.value = UiResult.Loading

        viewModelScope.launch {
            try {
                val comments = commentRepo.getCommentsWithUserName(reviewID)
                if (comments.isNotEmpty()) {
                    _comments.value = UiResult.Success(comments)
                } else {
                    _comments.value = UiResult.Error("No comments found")
                }
            } catch (e: Exception) {
                _comments.value = UiResult.Error(e.message ?: "Gagal memuat")
            }
        }
    }
}




