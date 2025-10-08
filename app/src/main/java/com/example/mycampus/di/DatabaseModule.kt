package com.example.mycampus.di

import android.content.Context
import androidx.room.Room
import com.example.mycampus.data.local.entity.AppDatabase
import com.example.mycampus.data.local.entity.NewsDao
import com.example.mycampus.data.local.entity.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mycampus.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideNewsDao(database: AppDatabase): NewsDao {
        return database.newsDao()
    }
    @Provides
    fun provideReportDao(database: AppDatabase): ReportDao {
        return database.reportDao()
    }
}