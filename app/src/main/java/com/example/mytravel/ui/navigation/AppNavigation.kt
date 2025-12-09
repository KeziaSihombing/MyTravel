package com.example.mytravel.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mytravel.ui.components.NavigationBar
import com.example.mytravel.ui.pages.ListCommentsScreen
import com.example.mytravel.ui.pages.LoginScreen
import com.example.mytravel.ui.pages.HomeScreen
import com.example.mytravel.ui.pages.RegisterScreen
import com.example.mytravel.ui.pages.ProfileScreen
import com.example.mytravel.ui.viewmodel.AuthViewModel
import com.example.mytravel.ui.viewmodel.HomeViewModel
import com.example.mytravel.ui.viewmodel.ProfileViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    listCommentViewModel: ProfileViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(isAuthenticated, navController) {
        if (!isAuthenticated) {
            Log.d("AppNavigation", "DEBUG: User authenticated → Login")
            navController.navigate(AppRoute.Register.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
        else {
            // ...paksa navigasi ke Home
            Log.d("AppNavigation", "DEBUG: User authenticated → Home")
            navController.navigate(AppRoute.Home.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in listOf(
        AppRoute.Login.route,
        AppRoute.Register.route,
        AppRoute.AddComment.route,
    )
    Log.d("ROUTE_CHECK", "Current Route: $currentRoute")
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    navController = navController,
                    selectedItem = currentRoute ?: ""
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Register.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoute.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateRegister = {
                        navController.navigate(AppRoute.Register.route)
                    },
                    onNavigateProfile = {
                        navController.navigate(AppRoute.Profile.route)
                    },
                    onNavigateHome = {
                        navController.navigate(AppRoute.Home.route)
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
                    viewModel = profileViewModel,
                    onLogout = {
                        navController.navigate(AppRoute.Login.route) {
                            popUpTo(AppRoute.Profile.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(AppRoute.ListComment.route) {
                ListCommentsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateAddComment = {
                        navController.navigate(AppRoute.AddComment.route)
                    }
                )
            }
            composable(AppRoute.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel
                )
            }
        }
    }
}


