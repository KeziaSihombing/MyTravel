package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.DiaryRepository
import com.example.mytravel.domain.model.DiaryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListDiaryViewModel : ViewModel() {
    private val repository = DiaryRepository()

    private val _diaries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val diaries: StateFlow<List<DiaryEntry>> = _diaries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadDiaries()
    }

    fun loadDiaries() {
        viewModelScope.launch {
            _isLoading.value = true
            _diaries.value = repository.getAllDiaries()
            _isLoading.value = false
        }
    }

    fun deleteDiary(id: Int) {
        viewModelScope.launch {
            val success = repository.deleteDiary(id)
            if (success) {
                loadDiaries()
            }
        }
    }
}