package com.example.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.local.entity.NewsEntity.Companion.TABLE_NAME

@Dao
interface NewsDao {
    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getNews(): List<NewsEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id =:newsId")
    suspend fun getNewsById(newsId: String): NewsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Query("SELECT * FROM $TABLE_NAME WHERE title LIKE '%' || :query || '%'")
    suspend fun getNewsBySearchQuery(query: String): List<NewsEntity>
}
