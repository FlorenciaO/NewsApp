package com.example.newsapp.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.use_cases.GetAllUsers
import com.example.newsapp.utils.ErrorCode
import com.example.newsapp.utils.ErrorCode.UNEXPECTED_ERROR
import com.example.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class UsersViewModel @Inject constructor(private val getAllUsers: GetAllUsers) : ViewModel() {

    private val _usersListState = MutableStateFlow(UsersListState())
    val usersListState = _usersListState.asStateFlow()

    init {
        getUsers()
    }

    private fun getUsers() {
        getAllUsers().onEach {
            when (it) {
                is Resource.Success -> {
                    _usersListState.value = UsersListState(users = it.data ?: emptyList())
                }

                is Resource.Error -> informErrorCode(it.errorCode)
                is Resource.Loading -> showLoading()
            }
        }.launchIn(viewModelScope)
    }

    private fun showLoading() {
        _usersListState.value = UsersListState(isLoading = true)
    }

    private fun informErrorCode(errorCode: ErrorCode?) {
        _usersListState.value = UsersListState(error = errorCode ?: UNEXPECTED_ERROR)
    }
}

