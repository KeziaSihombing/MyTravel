package com.example.mytravel.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoginSuccess by mutableStateOf(false)
        private set

    fun onEmailChanged(value: String) {
        email = value
    }

    fun onPasswordChanged(value: String) {
        password = value
    }

    fun login(){
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoginSuccess = true
        }
    }
}