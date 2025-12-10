package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.DestinationCard
import com.example.mytravel.ui.viewmodel.DestinationViewModel

@Composable
fun DestinationListScreen(
    viewModel: DestinationViewModel,
    onDetail: (Long) -> Unit
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
        Text(
            text = "All Destinations",
            style = MaterialTheme.typography.headlineSmall
        )

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
