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

    // Budget Routes
    object ListBudget : AppRoute("listBudget")
    object RincianBudget : AppRoute("rincianBudget/{rencanaId}") {
        fun createRoute(rencanaId: String) = "rincianBudget/$rencanaId"
    }
    object BuatBudget : AppRoute("buatBudget/{rencanaId}") {
        fun createRoute(rencanaId: String) = "buatBudget/$rencanaId"
    }

    // Screen lainnya
    object AddFirstProfile : AppRoute("addFirstProfile/{userId}"){
        fun build(id: String) = "addFirstProfile/$id"
    }
    // Screen Fitur Comment
    object AddComment : AppRoute("addComment/{reviewId}"){
        fun build(id: String) = "addComment/$id"
    }
    object ListComment : AppRoute("listComment/{reviewId}") {
        fun build(id: String) = "listComment/$id"
    }
    object CommentDetail : AppRoute("commentDetail/{commentId}") {
        fun build(id:String) = "commentDetail/$id"
    }

    // Screen Destinasi
    object DestinationList : AppRoute("listDestinations")
    object DestinationDetail : AppRoute("destinationDetail/{id}") {
        fun createRoute(id: Long) = "destinationDetail/$id"
    }

    // Review Routes
    object ReviewDetail : AppRoute("reviewDetail/{reviewId}") {
        fun createRoute(reviewId: Long) = "reviewDetail/$reviewId"
    }
    object AddReview : AppRoute("addReview/{id}") {
        fun createRoute(id: Long) = "addReview/$id"
    }

    object AddDiary : AppRoute("addDiary/{reviewId}"){
        fun build(id: String) = "addDiary/$id"
    }
    object DetailDiary: AppRoute("detailDiary/{diaryId}"){
        fun build(id: Int) = "detailDiary/$id"
    }
}
