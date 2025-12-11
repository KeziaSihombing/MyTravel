package com.example.mytravel.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.domain.model.CommentWithUserName
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.CommentItem
import com.example.mytravel.ui.viewmodel.CommentViewModel
import com.example.mytravel.ui.viewmodel.ReviewViewModel

@Composable
fun ListCommentsScreen(
    viewModel: CommentViewModel = viewModel(),
    viewModelTwo: ReviewViewModel = viewModel(),
    onNavigateBack: (Long) -> Unit,
    onNavigateAddComment: () -> Unit,
    onNavigateCommentDetail: (id: Long) -> Unit,
    reviewId: Long
) {
    val comments by viewModel.comments.collectAsState()
    val detailState by viewModelTwo.reviewDetail.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCommentsWithUserName(reviewId)
        viewModelTwo.loadReviewDetail(reviewId)
    }

    when(val state = detailState){
        is UiResult.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator(

                )
            }
        }
        is UiResult.Success -> {
            val review = state.data
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 30.dp),
            ){
                Row(
                    modifier = Modifier
                        .clickable { onNavigateBack(review.destinationId) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Kembali",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text=review.userName,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text=review.content,
                    fontSize = 15.sp,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Icon(
                    Icons.AutoMirrored.Filled.Comment,
                    tint = Color.Blue,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onNavigateAddComment() },
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Komentar",
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))
                when (val result = comments) {
                    is UiResult.Error -> {
                        val msg = result.message
                        Text("Error: ${msg}")
                    }
                    is UiResult.Success -> {
                        CommentList(
                            comments = result.data,
                            onNavigateCommentDetail = onNavigateCommentDetail
                        )
                    }
                    is UiResult.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ){
                            CircularProgressIndicator(

                            )
                        }
                    }
                }


            }
        }
        is UiResult.Error -> {
            Text("Error: ${(comments as UiResult.Error).message}")
        }

        else -> {}
    }
}

@Composable
fun CommentList(
    comments: List<CommentWithUserName>,
    onNavigateCommentDetail: (id:Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(comments.size) { index ->
            CommentItem(
                comment = comments[index],
                onNavigateCommentDetail = { id ->
                    onNavigateCommentDetail(id)
                }
            )
        }
    }
}
