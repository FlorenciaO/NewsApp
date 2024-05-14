package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.NO_COINCIDENCE
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.Resource.Error
import com.example.newsapp.utils.Resource.Loading
import com.example.newsapp.utils.Resource.Success
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFilteredNews @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(query: String): Flow<Resource<List<News>>> = flow {
        emit(Loading())
        val newsEntityList: List<NewsEntity> =
            newsRepository.getNewsBySearchQuery(forceDb = true, searchQuery = query)
        if (newsEntityList.isEmpty()) {
            emit(Error(NO_COINCIDENCE))
        } else {
            val userEntityList: List<UserEntity> =
                userRepository.getAllUsers(forceDb = true)
            val news: List<News> = newsEntityList.map { newsEntity ->
                val user: UserEntity? = userEntityList.find { it.id == newsEntity.userId }
                newsEntity.toNews(user?.userName ?: "")
            }
            emit(Success(news))
        }
    }
}
