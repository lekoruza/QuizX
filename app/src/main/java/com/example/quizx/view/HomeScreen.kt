package com.example.quizx.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quizx.viewmodel.QuizViewModel
import android.text.Html
import android.os.Build

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, quizViewModel: QuizViewModel = viewModel()) {
    val questions by quizViewModel.questions.collectAsState()
    val error by quizViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        quizViewModel.fetchQuestions(amount = 5, difficulty = "easy")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuizX - Početna") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Button(onClick = {
                navController.navigate("quiz")
            }) {
                Text("Započni kviz")
            }

            Button(onClick = {
                navController.navigate("result")
            }) {
                Text("Pogledaj rezultat")
            }

            if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            } else if (questions.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(questions) { q ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "Pitanje:", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                        Html.fromHtml(q.question, Html.FROM_HTML_MODE_LEGACY).toString()
                                    else
                                        Html.fromHtml(q.question).toString(),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Točan odgovor: " + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                        Html.fromHtml(q.correct_answer, Html.FROM_HTML_MODE_LEGACY).toString()
                                    else
                                        Html.fromHtml(q.correct_answer).toString(),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}