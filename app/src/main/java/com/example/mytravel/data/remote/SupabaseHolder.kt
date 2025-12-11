package com.example.mytravel.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseHolder {
    private const val SUPABASE_URL = "https://uihcobvkdhhstseoiurg.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpaGNvYnZrZGhoc3RzZW9pdXJnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ3NDk3MjUsImV4cCI6MjA4MDMyNTcyNX0.ypmy4gsA9BD6HaO_Os6h_bj_2P2VZCAFNCPPPySXHr0"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }

    fun session(): UserSession? {
        return client.auth.currentSessionOrNull()
    }
}