package com.example.newsapp.domain.repository

import com.example.newsapp.data.local.entity.NewsEntity

interface NewsRepository {
    suspend fun getAllNews(forceDb: Boolean = false): List<NewsEntity>
    suspend fun getNewsById(newsId: String, forceDb: Boolean = false): NewsEntity
    suspend fun getNewsBySearchQuery(forceDb: Boolean = false, searchQuery: String): List<NewsEntity>
}
