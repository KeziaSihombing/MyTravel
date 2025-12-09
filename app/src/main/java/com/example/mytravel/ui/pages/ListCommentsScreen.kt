package com.example.mytravel.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Comment
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
import com.example.mytravel.ui.viewmodel.ListCommentViewModel

@Composable
fun ListCommentsScreen(
    viewModel: ListCommentViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateAddComment: () -> Unit
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
            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ){
                Row(
                    modifier = Modifier
                        .clickable { onNavigateBack() },
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
                Spacer(modifier = Modifier.width(15.dp))

                Icon(
                    Icons.Default.Comment,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onNavigateAddComment() }
                )
                Text(
                    text = "Komentar",
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(15.dp))
                CommentList((comments as UiResult.Success).data)
            }
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
