package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.mytravel.domain.model.Rencana
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPlanScreen(
    planId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToBudget: (String) -> Unit,
    onNavigateToDiary: (String) -> Unit,
    planViewModel: PlanViewModel = viewModel()
) {
    val planDetailState by planViewModel.planDetail.collectAsState()

    LaunchedEffect(planId) {
        planViewModel.loadPlanDetail(planId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Rencana") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (val result = planDetailState) {
                is UiResult.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiResult.Success -> {
                    result.data?.let { plan ->
                        PlanDetailContent(plan = plan, onNavigateToBudget, onNavigateToDiary)
                    } ?: Text("Rencana tidak ditemukan", modifier = Modifier.align(Alignment.Center))
                }
                is UiResult.Error -> {
                    Text(result.message, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun PlanDetailContent(
    plan: Rencana,
    onNavigateToBudget: (String) -> Unit,
    onNavigateToDiary: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ) {
            AsyncImage(
                model = plan.gambar,
                contentDescription = plan.judul,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Judul")
        Text(plan.judul)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Deskripsi")
        Text(plan.deskripsi)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Lampiran")
        Text(plan.gambar ?: "-") // Menampilkan URL gambar atau strip jika tidak ada
        Spacer(modifier = Modifier.height(8.dp))
        Text("Kapan kamu ingin mencapai ini?")
        Text(plan.targetDate ?: "-")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { onNavigateToBudget(plan.id.toString()) }, modifier = Modifier.weight(1f)) {
                Text("Budget")
            }
            Button(onClick = { onNavigateToDiary(plan.id.toString()) }, modifier = Modifier.weight(1f)) {
                Text("Diary")
            }
        }
    }
}
