package com.example.quizx.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("home") { HomeScreen(navController) }
        composable("quiz") { QuizScreen(navController) }
        composable("result") { ResultScreen(navController) }
        composable("login"){ LoginScreen(navController) }
    }
}