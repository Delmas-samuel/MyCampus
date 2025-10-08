package com.example.mycampus.data.repository


import com.example.mycampus.data.local.entity.NewsDao
import com.example.mycampus.data.mapper.toNews
import com.example.mycampus.data.mapper.toNewsEntity
import com.example.mycampus.data.remote.NewsApi
import com.example.mycampus.domain.model.News
import com.example.mycampus.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao
) : NewsRepository {

    override fun getNews(): Flow<List<News>> {
        return newsDao.getAllNews().map { entities ->
            entities.map { it.toNews() }
        }
    }

    suspend fun refreshNews() {
        try {

            val remoteNews: List<com.example.mycampus.data.remote.dto.NewsDto> = newsApi.getNews()


            val newsEntities = remoteNews.map { it.toNewsEntity() }

            newsDao.deleteAllNews()
            newsDao.insertNews(newsEntities)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
