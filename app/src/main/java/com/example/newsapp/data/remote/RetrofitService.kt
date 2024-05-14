package com.example.newsapp.data.remote

import com.example.newsapp.data.remote.dto.NewsDto
import com.example.newsapp.data.remote.dto.UserDto
import retrofit2.http.GET

interface RetrofitService {
    @GET("/posts")
    suspend fun getAllNews(): List<NewsDto>
    @GET("/users")
    suspend fun getAllUsers(): List<UserDto>

    companion object {
        const val BASE_URL = "https://jsonplaceholder.org/"
    }
}
