package com.example.newsapp.data.repository

import com.example.newsapp.data.local.dao.UserDao
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.data.remote.RetrofitService
import com.example.newsapp.data.remote.dto.UserDto
import com.example.newsapp.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val retrofitService: RetrofitService
) : UserRepository {

    override suspend fun getAllUsers(forceDb: Boolean): List<UserEntity> {
        if (!forceDb) {
            callApiAndSaveData()
        }
        return userDao.getUsers()
    }

    override suspend fun getUserById(userId: String, forceDb: Boolean): UserEntity {
        if (!forceDb) {
            callApiAndSaveData()
        }
        return userDao.getUserById(userId)
    }

    private suspend fun callApiAndSaveData() {
        val userDtoList: List<UserDto> = retrofitService.getAllUsers()
        val userEntityList: List<UserEntity> = userDtoList.map { it.toUserEntity() }
        userDao.insertUsers(userEntityList)
    }
}
