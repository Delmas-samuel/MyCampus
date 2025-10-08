package com.example.mycampus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class News(
    val id: Int,
    val title: String,
    val date: String,
    val description: String,
    val imageUrl: String? = null
): Parcelable