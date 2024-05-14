package com.example.newsapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.data.local.entity.NewsEntity.Companion.TABLE_NAME
import com.example.newsapp.domain.model.News

@Entity(tableName = TABLE_NAME)
data class NewsEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo("user_id")
    val userId: String,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("content")
    val content: String,
    @ColumnInfo("image")
    val image: String,
    @ColumnInfo("thumbnail")
    val thumbnail: String,
    @ColumnInfo("updatedAt")
    val updatedAt: String
) {

    companion object {
        const val TABLE_NAME = "news_table"
    }

    fun toNews(byUser: String): News {
        return News(id, title, content, image, thumbnail, updatedAt, byUser)
    }
}
