package com.example.quizx.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizx.model.Category
import com.example.quizx.model.Question
import com.example.quizx.network.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import com.example.quizx.model.QuizResult

enum class SortOption {
    DATE, ACCURACY
}

class QuizViewModel : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _userAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val userAnswers: StateFlow<Map<Int, String>> = _userAnswers

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories
    private var currentCategoryName: String = "Any"

    private val _results = MutableStateFlow<List<QuizResult>>(emptyList())
    val results: StateFlow<List<QuizResult>> = _results

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _isLoadingQuestions = MutableStateFlow(false)
    val isLoadingQuestions: StateFlow<Boolean> = _isLoadingQuestions

    private val _sortOption = MutableStateFlow(SortOption.DATE)
    val sortOption: StateFlow<SortOption> = _sortOption


    fun fetchQuestions(amount: Int, difficulty: String?, category: Int?) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getQuestions(amount, difficulty, category)
                _questions.value = response.results
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Greska pri dohvacanju pitanja: ${e.localizedMessage}"
            }finally {
                _isLoadingQuestions.value = false
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCategories()
                _categories.value = response.trivia_categories
            } catch (e: Exception) {
                Log.e("FETCH_CATEGORIES", "Greska pri dohvacanju kategorija: ${e.localizedMessage}")
            }
        }
    }

    fun setCurrentCategoryName(name: String) { currentCategoryName = name }

    fun getCurrentCategoryName(): String = currentCategoryName

    fun saveUserAnswer(index: Int, answer: String) {
        _userAnswers.value = _userAnswers.value.toMutableMap().apply {
            put(index, answer)
        }
    }

    //firebase
    fun saveResult(score: Int, total: Int, categoryName: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        Log.d("FETCH_RESULTS", "Fetching results for uid: $uid")

        val resultData = hashMapOf(
            "score" to score,
            "total" to total,
            "timestamp" to Date(),
            "categoryName" to categoryName
        )

        db.collection("results")
            .document(uid)
            .collection("history")
            .add(resultData)
            .addOnSuccessListener {
                Log.d("SAVE_RESULT", "Successfully saved result.")
            }
            .addOnFailureListener {
                Log.e("SAVE_RESULT", "Error saving result: ${it.localizedMessage}")
            }
    }

    fun fetchResults() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()


        db.collection("results")
            .document(uid)
            .collection("history")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val resultList = querySnapshot.documents.mapNotNull { doc -> doc.toObject(QuizResult::class.java) }
                _results.value = resultList
                sortResults()
            }


    }

    private fun sortResults() {
        val sorted = when (_sortOption.value) {
            SortOption.DATE -> _results.value.sortedByDescending { it.timestamp }
            SortOption.ACCURACY -> _results.value.sortedByDescending {
                it.score.toFloat() / it.total.toFloat()
            }

        }
        _results.value = sorted
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        sortResults()
    }



}