package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.ProfileRepository
import com.example.mytravel.domain.model.Profile
import com.example.mytravel.ui.common.UiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val repo: ProfileRepository = ProfileRepository()
): ViewModel() {

    private val _profile = MutableStateFlow<UiResult<List<Profile>>>(UiResult.Loading)
    val profile: StateFlow<UiResult<List<Profile>>> = _profile

    fun getProfile(){
        _profile.value = UiResult.Loading

        viewModelScope.launch {
            try {
                val profile = repo.fetchProfile()
                if (profile != null) {
                    _profile.value = UiResult.Success(listOf(profile))
                } else {
                    _profile.value = UiResult.Error("Profile not found")
                }
            } catch (e: Exception) {
                _profile.value = UiResult.Error(e.message ?: "Gagal memuat")
            }
        }
    }

}