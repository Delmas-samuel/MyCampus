package com.example.mycampus.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val department: String? = null,
    val role: String,
    val avatarColor: Long = 0
)
