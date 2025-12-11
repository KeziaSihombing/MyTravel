package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.DiaryRepository
import com.example.mytravel.domain.model.DiaryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailDiaryViewModel : ViewModel() {
    private val repository = DiaryRepository()

    private val _diary = MutableStateFlow<DiaryEntry?>(null)
    val diary: StateFlow<DiaryEntry?> = _diary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    fun loadDiary(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _diary.value = repository.getDiaryById(id)
            _isLoading.value = false
        }
    }

    fun deleteDiary(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteDiary(id)
            _isLoading.value = false
            _deleteSuccess.value = success
        }
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }
}
