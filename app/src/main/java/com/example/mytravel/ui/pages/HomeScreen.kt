package com.example.mytravel.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.components.DestinationCard
import com.example.mytravel.ui.viewmodel.DestinationViewModel
import com.example.mytravel.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    destinationViewModel: DestinationViewModel = viewModel(),
    onShowMore: () -> Unit = {},
    onDetail: (Long) -> Unit = {}
) {

    val homeState by homeViewModel.homeProfile.collectAsState()
    val destinationsState by destinationViewModel.destinations.collectAsState()

    LaunchedEffect(Unit) {
        destinationViewModel.loadDestinations()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "MyTravel",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFF9C27B0), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            when (homeState) {
                is UiResult.Loading -> {
                    Text(
                        text = "Loading...",
                        color = Color.White
                    )
                }

                is UiResult.Error -> {
                    Text(
                        text = "Gagal memuat data",
                        color = Color.White
                    )
                }

                is UiResult.Success -> {
                    val name = (homeState as UiResult.Success).data
                    Column {
                        Text(
                            text = "Selamat Datang, $name",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Mau liburan kemana, nih?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fake Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search...") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Destinasi Wisata", style = MaterialTheme.typography.titleMedium)

            // Show more — tampil hanya kalau lebih dari 2 destinasi
            val list = (destinationsState as? UiResult.Success)?.data
            if (list != null && list.size > 2) {
                Text(
                    text = "Show more",
                    color = Color.Blue,
                    modifier = Modifier
                        .clickable { onShowMore() }
                        .padding(4.dp)
                )
            }
        }

        when (destinationsState) {
            is UiResult.Loading -> {
                Text("Loading destinations...")
            }

            is UiResult.Error -> {
                val msg = (destinationsState as UiResult.Error).message
                Text("Gagal memuat destinasi: $msg")
            }

            is UiResult.Success -> {
                val list = (destinationsState as UiResult.Success).data

                if (list.isEmpty()) {
                    Text("Tidak ada destinasi tersedia")
                } else {
                    list.take(2).forEach { dest ->
                        DestinationCard(
                            destination = dest,
                            onDetailClick = { onDetail(dest.id) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}