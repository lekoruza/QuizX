package com.example.quizx.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.quizx.viewmodel.QuizViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizx.R
import com.example.quizx.util.decodeHtml
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavController, quizViewModel: QuizViewModel = viewModel()) {
    val questions by quizViewModel.questions.collectAsState()
    val userAnswers by quizViewModel.userAnswers.collectAsState()

    val correctAnswers = questions.indices.count { i ->
        userAnswers[i] == questions[i].correct_answer
    }
    val incorrectAnswers = questions.size - correctAnswers
    val accuracy = if (questions.isNotEmpty()) (correctAnswers * 100) / questions.size else 0

    var expandedIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.diz),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Quiz Review") },
                    actions = {
                        TextButton(onClick = {
                            scope.launch {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }) {
                            Text("Home")
                        }

                        TextButton(onClick = {
                            scope.launch {
                                FirebaseAuth.getInstance().signOut()
                                delay(300)
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }) {
                            Text("Log out")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                item {
                    Text("Accuracy: $accuracy%", style = MaterialTheme.typography.displayMedium , color = Color.White)
                    Text(
                        "Correct: $correctAnswers | Incorrect: $incorrectAnswers",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                itemsIndexed(questions) { index, question ->
                    val userAnswer = userAnswers[index]
                    val correctAnswer = question.correct_answer
                    val isCorrect = userAnswer == correctAnswer

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                expandedIndex = if (expandedIndex == index) null else index
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Question ${index + 1}: ${decodeHtml(question.question)}")
                            if (expandedIndex == index) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Your answer: $userAnswer",
                                    color = if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                )
                                if (!isCorrect) {
                                    Text("Correct answer: $correctAnswer")
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("result") },
                        modifier = Modifier
                            .width(200.dp)
                    ) {
                        Text("View Results", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
