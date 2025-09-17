package com.example.quizx.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.quizx.viewmodel.QuizViewModel

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    val quizViewModel: QuizViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("home") { HomeScreen(navController, quizViewModel) }
        composable("quiz") { QuizScreen(navController, quizViewModel) }
        composable("login") { LoginScreen(navController) }
        composable("result") { ResultScreen(navController, quizViewModel) }
        composable("review") { ReviewScreen(navController, quizViewModel) }
    }
}


