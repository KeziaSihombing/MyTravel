package com.example.mytravel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mytravel.ui.navigation.AppNavigation
import com.example.mytravel.ui.pages.BuatDiaryScreen
import com.example.mytravel.ui.pages.HomeScreen
import com.example.mytravel.ui.pages.LoginScreen
import com.example.mytravel.ui.pages.RegisterScreen
import com.example.mytravel.ui.theme.MyTravelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}
