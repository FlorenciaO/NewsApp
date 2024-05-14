package com.example.newsapp.ui.users

import com.example.newsapp.domain.model.User
import com.example.newsapp.utils.ErrorCode

data class UsersListState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: ErrorCode? = null
)
