package com.example.quizx.network

import com.example.quizx.model.CategoryResponse
import com.example.quizx.model.QuestionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("difficulty") difficulty: String? = null,
        @Query("category") category: Int? = null
    ): QuestionResponse

    @GET("api_category.php")
    suspend fun getCategories(): CategoryResponse

}