package com.example.mycampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val date: String,
    val description: String,
    val imageUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)