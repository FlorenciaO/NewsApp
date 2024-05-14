package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.User
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

class GetAllUsers @Inject constructor(private val repo: UserRepository) {

    operator fun invoke(): Flow<Resource<List<User>>> = flow {
        emit(Loading())
        try {
            val userEntityList: List<UserEntity> = repo.getAllUsers(forceDb = true)
            val users: List<User> = userEntityList.map { it.toUser() }
            emit(Success(users))
        } catch (e: Exception) {
            emit(Error(UNEXPECTED_ERROR))
        }
    }
}
