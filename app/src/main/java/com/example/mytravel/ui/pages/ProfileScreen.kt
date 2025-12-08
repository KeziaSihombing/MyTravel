package com.example.mytravel.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLogout: () -> Unit
) {
    val profileState by viewModel.profile.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getProfile()
    }

    when (profileState) {
        is UiResult.Loading -> {
            CircularProgressIndicator()
        }
        is UiResult.Success -> {
            val profile = (profileState as UiResult.Success).data.firstOrNull()

            Column(
                modifier = Modifier.fillMaxSize().background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF6A1B9A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Text", color = Color.White)
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow("Nama", profile?.name ?: "Nama tidak tersedia")
                        InfoRow("Email", profile?.email ?: "Email tidak tersedia")
                        InfoRow("Deskripsi", profile?.description ?: "Deskripsi tidak tersedia")
                    }
                }
            }
        }
        is UiResult.Error -> {
            Text("Error: ${(profileState as UiResult.Error).message}")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

