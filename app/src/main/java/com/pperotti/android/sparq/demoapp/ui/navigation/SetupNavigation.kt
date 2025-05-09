package com.pperotti.android.sparq.demoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pperotti.android.sparq.demoapp.ui.main.MainScreen

@Composable
fun SetupNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home_screen"
    ) {
        composable(route = "home_screen") {
            MainScreen(
                onItemSelected = { id ->
                    println("Selected ID: $id")
                    //navController.navigate("details_screen/?id=$id")
                }
            )
        }
    }
}