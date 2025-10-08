package com.example.mycampus.data.remote

import com.example.mycampus.data.remote.model.OrsDirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RoutingApi {
    // Exemple simple avec start/end. Voir docs ORS; le param "start" et "end" attendent "lon,lat"
    @GET("v2/directions/driving-car")
    suspend fun route(
        @Query("api_key") apiKey: String,
        @Query("start") startLonLat: String, // "lon,lat"
        @Query("end") endLonLat: String      // "lon,lat"
    ): OrsDirectionsResponse
}
