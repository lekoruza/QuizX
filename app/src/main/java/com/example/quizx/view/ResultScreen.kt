package com.example.quizx.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizx.viewmodel.QuizViewModel
import com.example.quizx.viewmodel.SortOption
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.quizx.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, quizViewModel: QuizViewModel = viewModel()) {
    val results by quizViewModel.results.collectAsState()
    val sortOption by quizViewModel.sortOption.collectAsState()
    val scope = rememberCoroutineScope()


    val format = remember {
        SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
    }

    LaunchedEffect(Unit) {
        quizViewModel.fetchResults()
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
                    title = { Text("Quiz Results") },
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
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Sort by: ", color = Color.White)
                        SortOption.entries.forEach { option ->
                            FilterChip(
                                selected = sortOption == option,
                                onClick = { quizViewModel.setSortOption(option) },
                                label = {
                                    Text(
                                        option.name.lowercase().replaceFirstChar { it.uppercase() })
                                }
                            )
                        }
                    }
                }

                if (results.isEmpty()) {
                    item {
                        Text("No saved results.", color = Color.White)
                    }
                } else {
                    items(results) { result ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Category: ${result.categoryName}")
                                Text("Score: ${result.score} / ${result.total}")
                                val accuracy = (result.score.toFloat() / result.total.toFloat()) * 100
                                Text("Accuracy: %.1f%%".format(accuracy))
                                Text("Date: ${format.format(result.timestamp)}")
                            }
                        }
                    }
                }


            }
        }
    }
}

