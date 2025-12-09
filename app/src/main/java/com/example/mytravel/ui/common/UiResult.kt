package com.example.mytravel.ui.common

sealed interface UiResult<out T> {
    object Loading : UiResult<Nothing>
    data class Success<T>(val data: T) : UiResult<T>
    data class Error(val message: String) : UiResult<Nothing>
}