package com.example.newsapp.domain.use_cases

import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.domain.model.User
import com.example.newsapp.domain.model.User.Address
import com.example.newsapp.domain.repository.UserRepository
import com.example.newsapp.utils.ErrorCode.UNEXPECTED_ERROR
import com.example.newsapp.utils.Resource
import com.example.newsapp.utils.Resource.Error
import com.example.newsapp.utils.Resource.Loading
import com.example.newsapp.utils.Resource.Success
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserAddress @Inject constructor(private val repo: UserRepository) {
    operator fun invoke(userId: String): Flow<Resource<Address>> = flow {
        emit(Loading())
        if (userId.isEmpty()) {
            emit(Error(UNEXPECTED_ERROR))
        } else {
            val user: UserEntity = repo.getUserById(userId = userId, forceDb = true)
            emit(Success(user.address))
        }
    }
}
