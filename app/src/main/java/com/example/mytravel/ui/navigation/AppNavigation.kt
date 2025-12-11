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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mytravel.domain.model.Profile
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.NavigationBar
import com.example.mytravel.ui.pages.ListCommentsScreen
import com.example.mytravel.ui.pages.AddCommentScreen
import com.example.mytravel.ui.pages.BuatDiaryScreen
import com.example.mytravel.ui.pages.CommentDetailScreen
import com.example.mytravel.ui.pages.DestinationDetailScreen
import com.example.mytravel.ui.pages.DestinationListScreen
import com.example.mytravel.ui.pages.DetailDiaryScreen
import com.example.mytravel.ui.pages.DetailReviewScreen
import com.example.mytravel.ui.pages.EditProfileScreen
import com.example.mytravel.ui.pages.FormReviewScreen
import com.example.mytravel.ui.pages.LoginScreen
import com.example.mytravel.ui.pages.HomeScreen
import com.example.mytravel.ui.pages.ListDiaryScreen
import com.example.mytravel.ui.pages.RegisterScreen
import com.example.mytravel.ui.pages.ProfileScreen
import com.example.mytravel.ui.viewmodel.AuthViewModel
import com.example.mytravel.ui.viewmodel.DestinationViewModel
import com.example.mytravel.ui.viewmodel.HomeViewModel
import com.example.mytravel.ui.viewmodel.ProfileViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
) {
    val navController = rememberNavController()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val profileState by profileViewModel.profile.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(AppRoute.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        } else {
            profileViewModel.getProfile()
            navController.navigate(AppRoute.Home.route)
        }
    }

    LaunchedEffect(profileState, isAuthenticated) {
        if (!isAuthenticated) return@LaunchedEffect
        if (profileViewModel.firstTimeHandled) return@LaunchedEffect

        when(profileState){
            is UiResult.Success -> {
                val profile = (profileState as UiResult.Success<List<Profile>>).data.first()

                if (profile.name == "Default Name") {
                    profileViewModel.firstTimeHandled = true
                    navController.navigate(AppRoute.AddFirstProfile.build(profile.id)) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    profileViewModel.firstTimeHandled = true
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }


    val hideRoutes = listOf(
        AppRoute.Login.route,
        AppRoute.Register.route,
        AppRoute.AddComment.route,
        AppRoute.ListComment.route,
        AppRoute.CommentDetail.route,
        AppRoute.AddFirstProfile.route,
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
            // Auth Routes
            composable(AppRoute.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateRegister = {
                        navController.navigate(AppRoute.Register.route)
                    }
                )
            }
            composable(AppRoute.AddFirstProfile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                EditProfileScreen(
                    viewModel = profileViewModel,
                    profileState = profileState,
                    userId = userId,
                    home = { navController.navigate(AppRoute.Home.route) }
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
                    }
                )
            }

            // Comment Routes
            composable(
                AppRoute.ListComment.route
            ) { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getString("reviewId")?.toLong() ?: 0L
                ListCommentsScreen(
                    reviewId = reviewId,
                    onNavigateBack = { id -> navController.navigate(AppRoute.DestinationDetail.createRoute(id)) },
                    onNavigateAddComment = { navController.navigate(AppRoute.AddComment.build(reviewId.toString())) },
                    onNavigateCommentDetail = {id -> navController.navigate(AppRoute.CommentDetail.build(id.toString()))}
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

            composable (
                AppRoute.CommentDetail.route
            ){backStackEntry ->
                val commentId = backStackEntry.arguments?.getString("commentId")?.toLong()?: 0L
                CommentDetailScreen(
                    commentId = commentId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )

            }

            // Home & Destinations Routes
            composable(AppRoute.Home.route) {
                HomeScreen(
                    onShowMore = { navController.navigate(AppRoute.DestinationList.route) },
                    onDetail = { id -> navController.navigate(AppRoute.DestinationDetail.createRoute(id)) }
                )
            }

            composable(AppRoute.DestinationList.route) {
                DestinationListScreen(
                    onDetail = { id -> navController.navigate(AppRoute.DestinationDetail.createRoute(id)) },
                    onNavigateBack = { navController.navigate(AppRoute.Home.route)}
                )
            }

            composable(
                route = AppRoute.DestinationDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->

                val destinationId = backStackEntry.arguments!!.getLong("id")

                DestinationDetailScreen(
                    destinationId = destinationId,
                    onNavigateBack = { navController.navigate(AppRoute.Home.route) },
                    onNavigateAddReview = { navController.navigate(AppRoute.AddReview.createRoute(destinationId)) },
                    onNavigateReviewDetail = { reviewId ->
                        navController.navigate(AppRoute.ReviewDetail.createRoute(reviewId))
                    },
                    onNavigateCommentList = { reviewId ->
                        navController.navigate(AppRoute.ListComment.build(reviewId.toString()))
                    },
                    onNavigateAddComment = { reviewId ->
                        navController.navigate(AppRoute.AddComment.build(reviewId.toString()))
                    },
                )
            }

            // Review Routes
            composable(
                route = AppRoute.ReviewDetail.route,
                arguments = listOf(navArgument("reviewId") { type = NavType.LongType })
            ) { backStackEntry ->

                val reviewId = backStackEntry.arguments!!.getLong("reviewId")

                DetailReviewScreen(
                    reviewId = reviewId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "addReview/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) {
                val id = it.arguments!!.getLong("id")
                FormReviewScreen(
                    destinationId = id,
                    onBack = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack()}
                )
            }

            composable(route = AppRoute.Diary.route) {
                ListDiaryScreen(
                    onNavigateToBuat = {
                        navController.navigate(AppRoute.AddDiary.route)
                    }
                )
            }

            composable(route= AppRoute.AddDiary.route){
                BuatDiaryScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = viewModel()
                )
            }

            composable(
                route = AppRoute.DetailDiary.route,
                arguments = listOf(
                    navArgument("diaryId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val diaryId = backStackEntry.arguments?.getInt("diaryId") ?: 0
                DetailDiaryScreen(
                    diaryId = diaryId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToEdit = { id ->
                        // TODO: Implement edit screen
                    }
                )
            }
        }
    }

}