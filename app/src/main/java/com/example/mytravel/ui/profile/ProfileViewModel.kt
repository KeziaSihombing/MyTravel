package com.example.mytravel.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mytravel.data.model.UserProfile

class ProfileViewModel: ViewModel() {
    var profile by mutableStateOf<UserProfile?>(null)
        private set

    init {
        loadProfile()
    }

    private fun loadProfile() {
        profile = UserProfile(

        )
    }
}