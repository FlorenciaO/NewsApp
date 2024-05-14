package com.example.newsapp.ui.user_map

import com.example.newsapp.domain.model.User.Address
import com.example.newsapp.utils.ErrorCode

data class UserAddressState(
    val isLoading: Boolean = false,
    val address: Address? = null,
    val error: ErrorCode? = null
)
