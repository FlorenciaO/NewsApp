package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.UNEXPECTED_ERROR
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.Resource.Error
import com.example.newsapp.utils.Resource.Loading
import com.example.newsapp.utils.Resource.Success
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetNewsDetails @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(newsId: String): Flow<Resource<News>> = flow {
        emit(Loading())
        if (newsId.isEmpty()) {
            emit(Error(UNEXPECTED_ERROR))
        } else {
            val newsEntity: NewsEntity = newsRepository.getNewsById(newsId = newsId, forceDb = true)
            val userEntity: UserEntity =
                userRepository.getUserById(userId = newsEntity.userId, forceDb = true)
            emit(Success(newsEntity.toNews(byUser = userEntity.userName)))
        }
    }
}
