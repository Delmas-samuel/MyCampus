package com.example.mycampus.domain.repository

import com.example.mycampus.domain.model.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(): Flow<List<News>>
}