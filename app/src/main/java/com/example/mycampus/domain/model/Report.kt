package com.example.mycampus.domain.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val mediaPath: String? = null,
    val mediaType: MediaType,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: ReportStatus = ReportStatus.PENDING
)

enum class MediaType {
    PHOTO,
    VIDEO,
    NONE
}

enum class ReportStatus {
    PENDING,
    IN_PROGRESS,
    RESOLVED
}
