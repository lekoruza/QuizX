package com.example.quizx.model

import java.util.Date

data class QuizResult(
    val score: Int = 0,
    val total: Int = 0,
    val timestamp: Date = Date(),
    val categoryName: String = ""
)
