package com.example.mytravel.data.repository

import android.util.Log
import com.example.mytravel.data.remote.SupabaseHolder
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class AuthRepository {
    private val auth: Auth get() = SupabaseHolder.client.auth
    private val postgrest get() = SupabaseHolder.client.postgrest
    suspend fun register(email: String, password: String) {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun login(email: String, password: String) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout() {
        auth.signOut()
    }

    val sessionStatus: Flow<SessionStatus>
        get() {
            // Gunakan operator onEach untuk melakukan logging
            return auth.sessionStatus.onEach { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        // Log sumber sesi di sini, di dalam Repository
                        Log.d("AuthRepo", "Authenticated from source: ${status.source}")
                    }
                    is SessionStatus.NotAuthenticated -> {
                        Log.d("AuthRepo", "Status: Not authenticated. Signed out: ${status.isSignOut}")
                    }
                    is SessionStatus.NetworkError -> {
                        Log.e("AuthRepo", "Status: Network error.")
                    }
                    is SessionStatus.LoadingFromStorage -> {
                        Log.d("AuthRepo", "Status: Loading from storage")
                    }
                    else -> {
                        Log.d("AuthRepo", "Unknown status: $status")
                    }
                }
            }
        }

    fun currentSession(): UserSession? = SupabaseHolder.session()

    suspend fun refreshSession() {
        val session = currentSession()
            ?: throw IllegalStateException("No session available to refresh")

        val refreshToken = session.refreshToken
            ?: throw IllegalStateException("Refresh token is null")

        try {
            auth.refreshSession(refreshToken) // pakai refreshToken
        } catch (e: Exception) {
            logout()
            throw e
        }
    }

}