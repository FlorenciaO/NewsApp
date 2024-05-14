package com.example.newsapp.ui.news_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.use_cases.GetNewsDetails
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
class NewsDetailsViewModel @Inject constructor(
    private val getNewsDetails: GetNewsDetails,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _newsDetailsState = MutableStateFlow(NewsDetailsState())
    val newsDetailsState = _newsDetailsState.asStateFlow()

    init {
        savedStateHandle.get<String>("news_id")?.let {
            getNewsById(it)
        }
    }

    private fun getNewsById(newsId: String) {
        getNewsDetails(newsId).onEach {
            when (it) {
                is Resource.Success -> {
                    _newsDetailsState.value = NewsDetailsState(details = it.data)
                }

                is Resource.Error -> informErrorCode(it.errorCode)
                is Resource.Loading -> showLoading()
            }
        }.launchIn(viewModelScope)
    }

    private fun showLoading() {
        _newsDetailsState.value = NewsDetailsState(isLoading = true)
    }

    private fun informErrorCode(errorCode: ErrorCode?) {
        _newsDetailsState.value = NewsDetailsState(error = errorCode ?: UNEXPECTED_ERROR)
    }
}
