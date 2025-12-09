package com.example.mytravel.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mytravel.data.repository.AuthRepository

class HomeViewModel(
    private val repo: AuthRepository = AuthRepository()
): ViewModel() {

}