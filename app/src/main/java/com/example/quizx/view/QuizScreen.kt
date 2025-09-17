package com.example.quizx.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.quizx.viewmodel.QuizViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizx.R
import com.example.quizx.util.decodeHtml
import com.example.quizx.util.ShakeDetector
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController, quizViewModel: QuizViewModel = viewModel()) {
    val questions by quizViewModel.questions.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showAnswer by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    if (questions.isEmpty()) {
        Text("No available questions.")
        return
    }

    val question = questions[currentIndex]
    var allAnswers by remember(question) {
        mutableStateOf(
            (question.incorrect_answers + question.correct_answer)
                .map { decodeHtml(it) }
                .shuffled()
        )
    }

    DisposableEffect(question) {
        val shakeDetector = ShakeDetector {
            allAnswers = allAnswers.shuffled()
        }

        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }

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
                    title = { Text("Question ${currentIndex + 1} / ${questions.size}") },
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = decodeHtml(question.question),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 10.dp),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(25.dp))

                allAnswers.forEach { answer ->
                    val isUserAnswer = answer == selectedAnswer
                    val isCorrectAnswer = answer == decodeHtml(question.correct_answer)

                    val resultSymbol = when {
                        !showAnswer -> ""
                        isUserAnswer && isCorrectAnswer -> "✔"
                        isUserAnswer && !isCorrectAnswer -> "❌"
                        else -> ""
                    }

                    Button(
                        onClick = {
                            selectedAnswer = answer
                            showAnswer = true
                            selectedAnswer?.let {
                                quizViewModel.saveUserAnswer(currentIndex, it)
                            }

                            if (answer == question.correct_answer) {
                                score++
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !showAnswer
                    ) {
                        Text(
                            text = "$answer $resultSymbol",
                            fontSize = 18.sp,
                            //fontWeight = FontWeight.Bold,
                            color = if (showAnswer && isCorrectAnswer) Color(0xFF4CAF50)
                            else if (showAnswer) Color(0xFFF44336)
                            else Color.Black,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()

                ) {
                    if (showAnswer) {
                        Spacer(
                            modifier = Modifier.height(50.dp).align(Alignment.CenterHorizontally)
                        )

                        Button(
                            onClick = {
                                selectedAnswer = null
                                showAnswer = false
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                } else {
                                    quizViewModel.saveResult(
                                        score,
                                        questions.size,
                                        quizViewModel.getCurrentCategoryName()
                                    )
                                    navController.navigate("review")
                                }
                            }, modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .width(200.dp)
                        ) {
                            Text(
                                if (currentIndex < questions.size - 1) "Next question" else "Finish quiz",
                                fontSize = 18.sp,
                            )
                        }
                    }
                }

            }
        }
    }
}

