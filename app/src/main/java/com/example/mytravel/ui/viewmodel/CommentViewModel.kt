package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.CommentRepository
import com.example.mytravel.domain.model.Comment
import com.example.mytravel.domain.model.CommentWithUserName
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CommentViewModel(
    private val commentRepo : CommentRepository = CommentRepository()
): ViewModel() {
    private val _comments = MutableStateFlow<UiResult<List<CommentWithUserName>>>(UiResult.Loading)
    val comments: StateFlow<UiResult<List<CommentWithUserName>>> = _comments

    private val _adding = MutableStateFlow<UiResult<Comment>?>(null)
    val adding: StateFlow<UiResult<Comment>?> = _adding

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

    fun getCommentDetail(){

    }

    fun AddComment(reviewID: Long){
        _comments.value = UiResult.Loading

        viewModelScope.launch {
            try {

            }catch (e: Exception){
                _comments.value = UiResult.Error(e.message ?: "Gagal memuat")
            }

        }
    }
}




