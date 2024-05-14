package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.UserEntity
import com.example.newsapp.data.local.entity.UserEntity.Companion.TABLE_NAME

@Dao
interface UserDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id =:userId")
    suspend fun getUserById(userId: String): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(news: List<UserEntity>)
}
