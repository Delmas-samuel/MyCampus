package com.example.mycampus.data.remote


import com.example.mycampus.data.remote.dto.NewsDto
import retrofit2.http.GET

interface NewsApi {
    @GET("news")
    suspend fun getNews(): List<NewsDto>
}