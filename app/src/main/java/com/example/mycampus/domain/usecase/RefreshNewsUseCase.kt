package com.example.mycampus.domain.usecase

import com.example.mycampus.data.repository.NewsRepositoryImpl // Utilisez l'implémentation ici
import javax.inject.Inject

class RefreshNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepositoryImpl // Injection de l'implémentation concrète
) {
    suspend operator fun invoke() {
        newsRepository.refreshNews()
    }
}
    