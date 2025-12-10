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
    object AddComment : AppRoute("addComment/{reviewId}"){
        fun build(id: String) = "addComment/$id"
    }

    object ListComment : AppRoute("listComment/{reviewId}") {
        fun build(id: String) = "listComment/$id"
    }

}