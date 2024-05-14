package com.example.newsapp.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.use_cases.GetAllNews
import com.example.newsapp.domain.use_cases.GetFilteredNews
import com.example.newsapp.utils.ErrorCode
import com.example.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getAllNews: GetAllNews,
    private val getFilteredNews: GetFilteredNews
) : ViewModel() {

    private val _newsListState = MutableStateFlow(NewsListState())
    val newsListState = _newsListState.asStateFlow()

    private var allNewsList = emptyList<News>()

    init {
        getNews()
    }

    fun onSearch(query: String) {
        getFilteredNews(query).onEach {
            when (it) {
                is Resource.Success -> {
                    _newsListState.value = NewsListState(newsList = it.data ?: emptyList())
                }

                is Resource.Error -> informErrorCode(it.errorCode)
                is Resource.Loading -> showLoading()
            }
        }.launchIn(viewModelScope)
    }

    fun resetList() {
        _newsListState.value = NewsListState(newsList = allNewsList)
    }

    private fun getNews() {
        getAllNews().onEach {
            when (it) {
                is Resource.Success -> {
                    allNewsList = it.data ?: emptyList()
                    _newsListState.value = NewsListState(newsList = allNewsList)
                }

                is Resource.Error -> informErrorCode(it.errorCode)
                is Resource.Loading -> showLoading()
            }
        }.launchIn(viewModelScope)
    }

    private fun showLoading() {
        _newsListState.value = NewsListState(isLoading = true)
    }

    private fun informErrorCode(errorCode: ErrorCode?) {
        _newsListState.value = NewsListState(error = errorCode ?: ErrorCode.UNEXPECTED_ERROR)
    }
}
