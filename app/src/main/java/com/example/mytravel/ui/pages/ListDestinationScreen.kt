package com.example.mytravel.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
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
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.DestinationCard
import com.example.mytravel.ui.viewmodel.DestinationViewModel

@Composable
fun DestinationListScreen(
    viewModel: DestinationViewModel,
    onDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.destinations.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDestinations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.clickable { onNavigateBack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Kembali",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }


        Spacer(Modifier.height(16.dp))

        when (val result = state) {

            is UiResult.Loading -> {
                Text("Sedang memuat destinasi...")
            }

            is UiResult.Error -> {
                Text("Gagal memuat data: ${result.message}")
            }

            is UiResult.Success -> {
                val list = result.data

                LazyColumn {
                    items(list) { dest ->
                        DestinationCard(
                            destination = dest,
                            onDetailClick = { id -> onDetail(id) }
                        )

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
