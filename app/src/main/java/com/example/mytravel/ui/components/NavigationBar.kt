package com.example.mytravel.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mytravel.ui.navigation.AppRoute

@Composable
fun NavigationBar(
    navController: NavController,
    selectedItem: String
){
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedItem == AppRoute.Home.route,
            onClick = {
                navController.navigate(AppRoute.Home.route)
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedItem == AppRoute.Plan.route,
            onClick = {
//                navController.navigate(AppRoute.Plan.route)
            },
            icon = { Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = "Plan") },
            label = { Text("Plan") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedItem == AppRoute.Diary.route,
            onClick = {
                navController.navigate(AppRoute.Diary.route)
            },
            icon = { Icon(Icons.Default.NoteAlt, contentDescription = "Diary") },
            label = { Text("Diary") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedItem == AppRoute.Budget.route,
            onClick = {
                navController.navigate(AppRoute.Budget.route)
            },
            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Budget") },
            label = { Text("Budget") },
            colors = navItemColors()
        )
        NavigationBarItem(
            selected = selectedItem == AppRoute.Profile.route,
            onClick = {
                navController.navigate(AppRoute.Profile.route)
            },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            colors = navItemColors()
        )
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.White,
    unselectedIconColor = Color(0xFF3A8BFA),
    selectedTextColor = Color(0xFF3A8BFA),
    unselectedTextColor = Color.Gray,
    indicatorColor = Color(0x633A8BFA)
)


@Preview(showBackground = true)
@Composable
fun NavigationBarPreview(){
    NavigationBar(navController = NavController(LocalContext.current), selectedItem = AppRoute.Home.route)
}
