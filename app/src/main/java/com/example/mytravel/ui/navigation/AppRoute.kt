package com.example.mytravel.ui.navigation

sealed class AppRoute(val route: String) {
    object Login : AppRoute("login")
    object Register : AppRoute("register")

    // Screen utama
    object Home : AppRoute("home")
    object Plan : AppRoute("plan")
    object Diary : AppRoute("diary")
    object Budget : AppRoute("budget")
    object Profile : AppRoute("profile")

    // Screen lainnya
    object ListComment : AppRoute("listComment")
    object AddComment : AppRoute("addComment")

}