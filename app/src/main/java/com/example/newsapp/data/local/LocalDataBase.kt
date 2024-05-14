package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.data.local.dao.NewsDao
import com.example.newsapp.data.local.dao.UserDao
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.UserEntity

@Database(
    entities =
    [
        UserEntity::class,
        NewsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LocalDataBase : RoomDatabase() {
    internal abstract val newsDao: NewsDao
    internal abstract val userDao: UserDao
}
