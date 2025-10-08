package com.example.mycampus.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("addressdetails") addressDetails: Int = 0
    ): List<NominatimResult>
}

data class NominatimResult(
    val display_name: String,
    val lat: String,
    val lon: String
)
