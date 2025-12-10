package com.example.mytravel.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.CommentRepository
import com.example.mytravel.domain.model.Comment
import com.example.mytravel.domain.model.CommentWithUserName
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class CommentViewModel(
    private val commentRepo: CommentRepository = CommentRepository()
) : ViewModel() {

    private val _comments = MutableStateFlow<UiResult<List<CommentWithUserName>>>(UiResult.Loading)
    val comments: StateFlow<UiResult<List<CommentWithUserName>>> = _comments

    private val _adding = MutableStateFlow<UiResult<Comment>?>(null)
    val adding: StateFlow<UiResult<Comment>?> = _adding

    // Ambil komentar beserta nama user
    fun getCommentsWithUserName(reviewID: Long) {
        _comments.value = UiResult.Loading

        viewModelScope.launch {
            try {
                val comments = commentRepo.getCommentsWithUserName(reviewID)
                if (comments.isNotEmpty()) {
                    _comments.value = UiResult.Success(comments)
                } else {
                    UiResult.Error("No comments found")
                }
            } catch (e: Exception) {
                _comments.value = UiResult.Error(e.message ?: "Gagal memuat komentar")
            }
        }
    }

    // Fungsi untuk menambah komentar
    fun addComment(reviewID: Long, komentar: String?, imageFiles: List<File>) {
        Log.d("ADD_COMMENT_VM", "CALLED — reviewID=$reviewID, files=${imageFiles.size}")
        _adding.value = UiResult.Loading

        viewModelScope.launch {
            try {
                val comment = commentRepo.addComment(reviewID, komentar, imageFiles)
                Log.d("ADD_COMMENT_VM", "SUCCESS: $comment")
                _adding.value = UiResult.Success(comment)

                // Update list komentar setelah menambahkan
                getCommentsWithUserName(reviewID)
            } catch (e: Exception) {
                Log.e("ADD_COMMENT_VM", "ERROR", e)
                _adding.value = UiResult.Error(e.message ?: "Gagal menambah komentar")
            }
        }
    }

    fun getCommentDetail() {
        // Bisa diimplementasikan nanti bila perlu detail komentar tertentu
    }
}
