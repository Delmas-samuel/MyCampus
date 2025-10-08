package com.example.mycampus.domain.usecase

import com.example.mycampus.data.repository.LocationRepository
import com.example.mycampus.domain.model.LatLng
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): LatLng? = locationRepository.getCurrentLocation()
}
