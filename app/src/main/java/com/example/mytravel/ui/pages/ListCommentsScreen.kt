package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.domain.model.CommentWithUserName
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.CommentItem
import com.example.mytravel.ui.viewmodel.ListCommentViewModel

@Composable
fun ListCommentsScreen(
    viewModel: ListCommentViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val comments by viewModel.comments.collectAsState()
    LaunchedEffect(Unit) {
        val reviewID = 1.toLong();
        viewModel.getCommentsWithUserName(reviewID)
    }

    when(comments){
        is UiResult.Loading -> {
            CircularProgressIndicator()
        }
        is UiResult.Success -> {
            CommentList((comments as UiResult.Success).data)
        }
        is UiResult.Error -> {
            Text("Error: ${(comments as UiResult.Error).message}")
        }
    }
}

@Composable
fun CommentList(comments: List<CommentWithUserName>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(comments.size) { index ->
            CommentItem(comment = comments[index])
        }
    }
}
