package com.example.newsapp.ui.news_details

import com.example.newsapp.domain.model.News
import com.example.newsapp.utils.ErrorCode

data class NewsDetailsState(
    val isLoading: Boolean = false,
    val details: News? = null,
    val error: ErrorCode? = null
)
