package com.example.mycampus.data.remote.dto

data class NewsDto(
    val id: Int,
    val title: String,
    val date: String,
    val description: String,
    val image: String?
)