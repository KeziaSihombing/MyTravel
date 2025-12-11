package com.example.mytravel.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytravel.domain.model.Profile
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    profileState: UiResult<List<Profile>>,
    home: () -> Unit,
    userId: String,
){
    val state by viewModel.editing.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is UiResult.Success) {
            home()
        }
    }

    when(profileState){
        is UiResult.Error -> {
            Text("Error: ${(profileState as UiResult.Error).message}")
        }

        UiResult.Loading -> {
            CircularProgressIndicator()
        }

        is UiResult.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Selamat Datang di MyTravel",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Isi keterangan diri Anda",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(15.dp))

                OutlinedTextField(
                    name,
                    {name = it},
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Masukkan nama Anda(Nama ini akan terlihat oleh user lain)") }
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    description,
                    {description = it},
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                    placeholder = {Text("Masukkan deskripsi akun Anda")}
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {viewModel.editProfile(userId, name, description) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                    Text(text ="Simpan", color = Color.White, fontSize = 16.sp)
                }
            }
            if (state is UiResult.Error) {
                Text(
                    text = (state as UiResult.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

        }

    }
}


