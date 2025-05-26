package com.example.quizx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizx.model.Question
import com.example.quizx.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun fetchQuestions(amount: Int, difficulty: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getQuestions(amount, difficulty)
                _questions.value = response.results
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Greška pri dohvaćanju pitanja: ${e.localizedMessage}"
            }
        }
    }
}