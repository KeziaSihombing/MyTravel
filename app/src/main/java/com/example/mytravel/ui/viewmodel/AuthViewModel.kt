package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytravel.data.repository.AuthRepository
import com.example.mytravel.ui.common.UiResult
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
): ViewModel(){

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _loginState = MutableStateFlow<UiResult<Unit>?>(null)
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<UiResult<Unit>?>(null)
    val registerState = _registerState.asStateFlow()

    init {
        viewModelScope.launch {
            _isAuthenticated.value = repo.isAuthenticated()
        }
    }

    fun getUserId(): String? {
        return com.example.mytravel.data.remote.SupabaseHolder.client.auth.currentUserOrNull()?.id
    }

    fun login(email: String, pass: String) {
        _loginState.value = UiResult.Loading
        viewModelScope.launch {
            try {
                repo.login(email, pass)
                _loginState.value = UiResult.Success(Unit)
                _isAuthenticated.value = true
            } catch (e: Exception) {
                _loginState.value = UiResult.Error(e.message ?: "Gagal login")
            }
        }
    }

    // Ubah fungsi register untuk menerima nama
    fun register(name: String, email: String, pass: String) {
        _registerState.value = UiResult.Loading
        viewModelScope.launch {
            try {
                repo.register(name, email, pass)
                _registerState.value = UiResult.Success(Unit)
            } catch (e: Exception) {
                _registerState.value = UiResult.Error(e.message ?: "Gagal register")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _isAuthenticated.value = false
        }
    }
}
