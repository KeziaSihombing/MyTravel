package com.example.mytravel.ui.pages


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.mytravel.ui.common.UiResult
import com.example.mytravel.ui.viewmodel.AuthViewModel


@Composable
fun RegisterScreen(
    viewModel: AuthViewModel
) {
    val state by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is UiResult.Error) {
            errorMessage = (state as UiResult.Error).message
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Register", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(email, { email = it }, label = { Text("Email") })
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
            OutlinedTextField(name, { name = it }, label = { Text("Name") })
            OutlinedTextField(description, { description = it }, label = { Text("Description") })
            Button(onClick = { viewModel.register(email, password, name, description) }, enabled = state !is UiResult.Loading) {
                Text(if (state is UiResult.Loading) "Loading..." else "Register")
            }
            if (state is UiResult.Error) Text((state as UiResult.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}