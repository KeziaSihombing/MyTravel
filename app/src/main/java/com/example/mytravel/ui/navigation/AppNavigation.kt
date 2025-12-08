package com.example.mytravel.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mytravel.ui.pages.LoginScreen
import com.example.mytravel.ui.pages.RegisterScreen
import com.example.mytravel.ui.pages.ProfileScreen
import com.example.mytravel.ui.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isAuthenticated, navController) {
        if (!isAuthenticated) {
            Log.d("AppNavigation", "DEBUG: User authenticated → Login")
            navController.navigate(AppRoute.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
        else {
            // ...paksa navigasi ke Home
            Log.d("AppNavigation", "DEBUG: User authenticated → Home")
            navController.navigate(AppRoute.Profile.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Register.route,
        modifier = modifier
    ) {

        composable(AppRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateRegister = {
                    navController.navigate(AppRoute.Register.route)
                },
                onNavigateProfile = {
                    navController.navigate(AppRoute.Profile.route)
                }
            )
        }

        composable(AppRoute.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateLogin = {
                    navController.navigate(AppRoute.Login.route)
                }
            )
        }

        composable(AppRoute.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Profile.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
