package com.example.mycampus.data.repository

import com.example.mycampus.data.remote.RoutingApi
import com.example.mycampus.domain.model.LatLng
import com.example.mycampus.domain.model.RouteSegment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutingRepository @Inject constructor(
    private val api: RoutingApi
) {
    suspend fun getRoute(
        apiKey: String,
        start: LatLng,
        end: LatLng
    ): RouteSegment? {
        val resp = api.route(
            apiKey = apiKey,
            startLonLat = "${start.longitude},${start.latitude}",
            endLonLat = "${end.longitude},${end.latitude}"
        )
        val coords = resp.features.firstOrNull()?.geometry?.coordinates ?: return null
        val points = coords.map { pair ->
            LatLng(latitude = pair[1], longitude = pair[0])
        }
        return RouteSegment(points = points)
    }
}