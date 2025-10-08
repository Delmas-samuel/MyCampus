package com.example.mycampus.domain.usecase

import com.example.mycampus.data.repository.GeocodeSuggestion
import com.example.mycampus.data.repository.GeocodingRepository
import javax.inject.Inject

class SearchPlacesUseCase @Inject constructor(
    private val geocodingRepository: GeocodingRepository
) {
    suspend operator fun invoke(query: String): List<GeocodeSuggestion> {
        return geocodingRepository.search(query)
    }
}
