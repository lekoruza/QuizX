package com.example.quizx.model

data class Question (
    val question: String,
    val correct_answer: String,
    val incorrect_answers: List<String>,
    val difficulty: String,
    val category: String,
    val type: String,
)