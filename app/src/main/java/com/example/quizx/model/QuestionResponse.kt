package com.example.quizx.model

data class QuestionResponse(
    val response_code: Int,
    val results: List<Question>
)
