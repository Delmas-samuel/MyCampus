package com.example.mycampus.data.remote.model

data class NewsResponse(
    val news: List<NewsDto>
)

data class NewsDto(
    val id: Int,
    val title: String,
    val date: String,
    val description: String,
    val image: String?
)