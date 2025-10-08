// CHEMIN : app/src/main/java/com/example/mycampus/domain/usecase/GetRouteToEsaticUseCase.kt
package com.example.mycampus.domain.usecase

import com.example.mycampus.data.repository.RoutingRepository
import com.example.mycampus.domain.model.LatLng
import com.example.mycampus.domain.model.RouteSegment
import javax.inject.Inject

class GetRouteToEsaticUseCase @Inject constructor(
    private val routingRepository: RoutingRepository
) {
    suspend operator fun invoke(
        apiKey: String,
        userLocation: LatLng,
        esatic: LatLng
    ): RouteSegment? {
        return routingRepository.getRoute(
            apiKey = apiKey,
            start = userLocation,
            end = esatic
        )
    }
}
