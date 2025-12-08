package com.example.mytravel.ui.navigation

sealed class AppRoute(val route: String) {
    object Login : AppRoute("login")
    object Profile : AppRoute("profile")
    object Home : AppRoute("home")
    object Register : AppRoute("register")
    object ListComment : AppRoute("listComment")
}