package com.example.newsapp.ui.user_map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.use_cases.GetUserAddress
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
class UserMapViewModel @Inject constructor(
    private val getUserAddress: GetUserAddress,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _addressState = MutableStateFlow(UserAddressState())
    val userAddressState = _addressState.asStateFlow()

    init {
        savedStateHandle.get<String>("user_id")?.let {
            getAddress(it)
        }
    }

    private fun getAddress(userId: String) {
        getUserAddress(userId).onEach {
            when (it) {
                is Resource.Success -> {
                    _addressState.value = UserAddressState(address = it.data)
                }

                is Resource.Error -> informErrorCode(it.errorCode)
                is Resource.Loading -> showLoading()
            }
        }.launchIn(viewModelScope)
    }

    private fun showLoading() {
        _addressState.value = UserAddressState(isLoading = true)
    }

    private fun informErrorCode(errorCode: ErrorCode?) {
        _addressState.value = UserAddressState(error = errorCode ?: UNEXPECTED_ERROR)
    }
}
