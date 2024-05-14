package com.example.newsapp.data.repository

import com.example.newsapp.data.local.dao.NewsDao
import com.example.newsapp.data.local.entity.NewsEntity
import com.example.newsapp.data.remote.RetrofitService
import com.example.newsapp.data.remote.dto.NewsDto
import com.example.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val retrofitService: RetrofitService
) : NewsRepository {
    override suspend fun getAllNews(forceDb: Boolean): List<NewsEntity> {
        if (!forceDb) {
            callApiAndSaveData()
        }
        return newsDao.getNews()
    }

    override suspend fun getNewsById(newsId: String, forceDb: Boolean): NewsEntity {
        if (!forceDb) {
            callApiAndSaveData()
        }
        return newsDao.getNewsById(newsId)
    }

    override suspend fun getNewsBySearchQuery(forceDb: Boolean, searchQuery: String): List<NewsEntity> {
        if (!forceDb) {
            callApiAndSaveData()
        }
        return if (searchQuery.isBlank() || searchQuery.isEmpty()) {
            newsDao.getNews()
        } else {
            newsDao.getNewsBySearchQuery(searchQuery)
        }
    }

    private suspend fun callApiAndSaveData() {
        val newsDtoList: List<NewsDto> = retrofitService.getAllNews()
        val newsEntityList: List<NewsEntity> = newsDtoList.map { it.toNewsEntity() }
        newsDao.insertNews(newsEntityList)
    }
}
