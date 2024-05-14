package com.example.newsapp.domain.model

data class News(
    val id: String,
    val title: String,
    val content: String,
    val image: String,
    val thumbnail: String,
    val updatedAt: String,
    val byUser: String
)
