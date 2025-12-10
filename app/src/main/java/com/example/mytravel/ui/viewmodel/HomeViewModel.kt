package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.AuthRepository
import com.example.mytravel.data.repository.ProfileRepository
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val profileRepo: ProfileRepository = ProfileRepository()
): ViewModel() {
    private val _homeProfile = MutableStateFlow<UiResult<String>>(UiResult.Loading)
    val homeProfile = _homeProfile.asStateFlow()

    init {
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            try {
                val profile = profileRepo.fetchProfile()
                val name = profile?.name ?: "User"
                _homeProfile.value = UiResult.Success(name)

            } catch (e: Exception) {
                _homeProfile.value = UiResult.Error("Gagal memuat data")
            }
        }
    }
}