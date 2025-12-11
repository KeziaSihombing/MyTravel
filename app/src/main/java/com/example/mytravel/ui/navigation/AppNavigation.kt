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
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.NavigationBar
import com.example.mytravel.ui.pages.ListCommentsScreen
import com.example.mytravel.ui.pages.AddCommentScreen
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
    homeViewModel: HomeViewModel = viewModel()
) {
    val navController = rememberNavController()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route



    LaunchedEffect(isAuthenticated, navController) {
        // Hanya lakukan auto-navigate jika user tidak sedang di layar auth
        val authScreens = listOf(AppRoute.Login.route, AppRoute.Register.route)

        Log.d("AppNavigation", "DEBUG: isAuthenticated = $isAuthenticated")

        if (!isAuthenticated) {
            Log.d("AppNavigation", "DEBUG: User NOT authenticated → Login")
            // ...paksa navigasi ke Login dan bersihkan semua back stack
            navController.navigate(AppRoute.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
        else {
            Log.d("AppNavigation", "DEBUG: User authenticated → Home")
            navController.navigate(AppRoute.Home.route) {
                popUpTo(AppRoute.Login.route) { inclusive = true }
            }
        }
    }

    val hideRoutes = listOf(
        AppRoute.Login.route,
        AppRoute.Register.route,
        AppRoute.AddComment.route,
        AppRoute.ListComment.route
    )

    val showBottomBar = hideRoutes.none { route ->
        currentRoute?.startsWith(route) == true
    }

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
            startDestination = AppRoute.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoute.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateRegister = {
                        navController.navigate(AppRoute.Register.route)
                    }
                )
            }

            composable(AppRoute.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(AppRoute.Login.route)
                    }
                )
            }
            composable(AppRoute.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = {
                        authViewModel.logout()
                    },
                    onCommentList = { reviewId ->
                        navController.navigate(AppRoute.ListComment.build(reviewId.toString()))
                    }
                )
            }

            composable(
                AppRoute.ListComment.route
            ) { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getString("reviewId")?.toLong() ?: 0L
                ListCommentsScreen(
                    reviewId = reviewId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateAddComment = { navController.navigate(AppRoute.AddComment.build(reviewId.toString())) }
                )
            }

            composable(AppRoute.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel
                )
            }
            composable (
                AppRoute.AddComment.route
            ) {backStackEntry ->
                val reviewId = backStackEntry.arguments?.getString("reviewId")?.toLong()?: 0L
                AddCommentScreen(
                    reviewId = reviewId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onDone = {
                        navController.navigate(AppRoute.ListComment.build(reviewId.toString()))
                    }
                )
            }
        }
    }
}