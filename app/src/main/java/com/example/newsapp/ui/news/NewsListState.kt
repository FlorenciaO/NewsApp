package com.example.newsapp.ui.news

import com.example.newsapp.domain.model.News
import com.example.newsapp.utils.ErrorCode

data class NewsListState(
    val isLoading: Boolean = false,
    val newsList: List<News> = emptyList(),
    val error: ErrorCode? = null
)
