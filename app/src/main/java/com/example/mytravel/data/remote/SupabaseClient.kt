package com.example.mytravel.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseHolder {
    // Ganti dengan URL & anon/public key Supabase Anda

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://uihcobvkdhhstseoiurg.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpaGNvYnZrZGhoc3RzZW9pdXJnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ3NDk3MjUsImV4cCI6MjA4MDMyNTcyNX0.ypmy4gsA9BD6HaO_Os6h_bj_2P2VZCAFNCPPPySXHr0"
    ) {
        install(Auth)
        install(Postgrest.Companion)
        install(Storage.Companion)
    }

    fun session(): UserSession? = client.auth.currentSessionOrNull()
}