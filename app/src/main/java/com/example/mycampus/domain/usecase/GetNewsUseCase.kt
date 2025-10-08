package com.example.mycampus.domain.usecase

import com.example.mycampus.domain.model.News
import com.example.mycampus.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    operator fun invoke(): Flow<List<News>> {
        return newsRepository.getNews()
    }
}


