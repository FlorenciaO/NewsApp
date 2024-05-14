package com.example.newsapp.data.remote.dto

import com.example.newsapp.data.local.entity.NewsEntity
import com.google.gson.annotations.SerializedName

data class NewsDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userId")
    val userId: String,
) {
    fun toNewsEntity(): NewsEntity {
        return NewsEntity(id, userId, title, content, image, thumbnail, updatedAt)
    }
}
