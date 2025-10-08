package com.example.mycampus.data.repository


import com.example.mycampus.data.local.entity.ReportDao
import com.example.mycampus.domain.model.Report
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {

    fun getAllReports(): Flow<List<Report>> = reportDao.getAllReports()

    suspend fun getReportById(reportId: Int): Report? = reportDao.getReportById(reportId)

    suspend fun insertReport(report: Report): Long = reportDao.insertReport(report)

    suspend fun updateReport(report: Report) = reportDao.updateReport(report)

    suspend fun deleteReport(report: Report) = reportDao.deleteReport(report)

    suspend fun deleteReportById(reportId: Int) = reportDao.deleteReportById(reportId)
}
