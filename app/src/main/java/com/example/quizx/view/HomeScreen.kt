package com.example.quizx.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.quizx.R
import com.example.quizx.model.Category
import com.example.quizx.viewmodel.QuizViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, quizViewModel: QuizViewModel = viewModel()) {
    var numberOfQuestions by remember { mutableStateOf("5") }
    var selectedDifficulty by remember { mutableStateOf("any") }
    val categories by quizViewModel.categories.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        quizViewModel.fetchCategories()
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
                    title = { Text("QuizX", color = Color.White) },
                    actions = {
                        TextButton(onClick = {
                            scope.launch {
                                navController.navigate("result") {
                                    popUpTo("result") { inclusive = true }
                                }
                            }
                        }) {
                            Text("Previous Results")
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
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                Text(
                    "Quiz Settings",
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier.padding(40.dp)
                        .align(alignment = Alignment.CenterHorizontally)
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory?.name ?: "Any",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Any") },
                                    onClick = {
                                        selectedCategory = null
                                        expanded = false
                                    }
                                )
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.name) },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = numberOfQuestions,
                            onValueChange = { numberOfQuestions = it },
                            label = { Text("Number of questions") },
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column {
                            Text("Difficulty", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                listOf("Any", "Easy", "Medium", "Hard").forEach { diff ->
                                    FilterChip(
                                        selected = selectedDifficulty.equals(diff, ignoreCase = true),
                                        onClick = { selectedDifficulty = diff.lowercase() },
                                        label = { Text(diff, fontSize = 14.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))

                Button(
                    onClick = {
                        val amount = numberOfQuestions.toIntOrNull() ?: 5
                        val difficulty =
                            if (selectedDifficulty == "any") null else selectedDifficulty
                        val categoryId = selectedCategory?.id
                        quizViewModel.setCurrentCategoryName(selectedCategory?.name ?: "Any")
                        quizViewModel.fetchQuestions(amount, difficulty, categoryId)

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(500)
                            navController.navigate("quiz")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(220.dp)
                        .height(50.dp)
                ) {
                    Text("Start Quiz", fontSize = 22.sp)
                }


            }
        }
    }
}
