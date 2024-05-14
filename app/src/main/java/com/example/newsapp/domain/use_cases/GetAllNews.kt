package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.News
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.CONNECTION_ERROR
import com.example.newsapp.utils.ErrorCode.UNEXPECTED_ERROR
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.Resource.Error
import com.example.newsapp.utils.Resource.Loading
import com.example.newsapp.utils.Resource.Success
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class GetAllNews @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userRepository: UserRepository
) {

    operator fun invoke(): Flow<Resource<List<News>>> = flow {
        try {
            emit(Loading())
            val newsEntityList: List<NewsEntity> = newsRepository.getAllNews(forceDb = false)
            val userEntityList: List<UserEntity> = userRepository.getAllUsers(forceDb = false)
            val news: List<News> = newsEntityList.map { newsEntity ->
                val user: UserEntity? = userEntityList.find { it.id == newsEntity.userId }
                newsEntity.toNews(user?.userName ?: "")
            }
            emit(Success(news))
        } catch (e: HttpException) {
            emit(Error(UNEXPECTED_ERROR))
        } catch (e: IOException) {
            emit(Error(CONNECTION_ERROR))
        }
    }
}
