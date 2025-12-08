package com.example.mytravel.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    // ⚠️ GANTI DENGAN URL & ANON KEY DARI SUPABASE DASHBOARD KAMU
    private const val SUPABASE_URL = "https://cqtcmvechsowlnrjevwt.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNxdGNtdmVjaHNvd2xucmpldnd0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjUxOTI1MDIsImV4cCI6MjA4MDc2ODUwMn0.7RmVXouGmf4Gu3JeVGNZc0d4YaviKyiYQHyMZ-ulYQQ"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}

