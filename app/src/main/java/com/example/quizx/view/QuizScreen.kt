package com.example.quizx.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Rješavanje kviza") }) }
    ) { padding ->
        Text(
            text = "Ovdje će biti kviz pitanja",
            modifier = Modifier.padding(padding)
        )
    }
}

