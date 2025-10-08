package com.example.mycampus.data.local.entity

import androidx.room.TypeConverter
import com.example.mycampus.domain.model.MediaType
import com.example.mycampus.domain.model.ReportStatus

// Cette classe contient les logiques de conversion pour les types que Room ne connaît pas
class Converters {
    // Convertisseur pour MediaType
    @TypeConverter
    fun fromMediaType(value: String) = MediaType.valueOf(value)

    @TypeConverter
    fun toMediaType(mediaType: MediaType) = mediaType.name

    // Convertisseur pour ReportStatus
    @TypeConverter
    fun fromReportStatus(value: String) = ReportStatus.valueOf(value)

    @TypeConverter
    fun toReportStatus(reportStatus: ReportStatus) = reportStatus.name
}
