package com.example.mytravel.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://uihcobvkdhhstseoiurg.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable__O0kD_BcAJyHVixgeg1oJg_HO-VemQg"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }

    fun session(): UserSession? {
        return client.auth.currentSessionOrNull()
    }
}
