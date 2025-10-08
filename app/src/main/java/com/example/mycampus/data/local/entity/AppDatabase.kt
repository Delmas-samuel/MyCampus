package com.example.mycampus.data.local.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mycampus.domain.model.Report

@Database(
    entities = [
        NewsEntity::class,
        Report::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun reportDao(): ReportDao
}
