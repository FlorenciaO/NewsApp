package com.example.newsapp.domain.repository

import com.example.newsapp.data.local.entity.UserEntity

interface UserRepository {
    suspend fun getAllUsers(forceDb: Boolean = false): List<UserEntity>
    suspend fun getUserById(userId: String, forceDb: Boolean = false): UserEntity
}
