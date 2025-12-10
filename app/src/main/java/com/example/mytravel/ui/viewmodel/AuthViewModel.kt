package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.AuthRepository
import com.example.mytravel.ui.common.UiResult
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<UiResult<Boolean>>(UiResult.Success(repo.currentSession() != null))
    val authState: StateFlow<UiResult<Boolean>> = _authState

    val isAuthenticated: StateFlow<Boolean> = repo.sessionStatus
        .map { status -> status is SessionStatus.Authenticated }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun register(email: String, password: String, name: String, description: String) {
        _authState.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val user = repo.register(email, password)
                _authState.value = UiResult.Success(true)
            } catch (e: Exception) {
                _authState.value = UiResult.Error(e.message ?: "Register gagal")
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = UiResult.Loading
        viewModelScope.launch {
            try {
                val user = repo.login(email, password)
                _authState.value = UiResult.Success(true)
            } catch (e: Exception) {
                _authState.value = UiResult.Error(e.message ?: "Login gagal")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repo.logout()
                _authState.value = UiResult.Success(false)
            }
            catch (e: Exception) {
                _authState.value = UiResult.Error(e.message ?: "Logout gagal")
            }}

    }


}