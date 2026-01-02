package com.example.valguard.app.dca.data

import com.example.valguard.app.dca.data.local.DCAExecutionDao
import com.example.valguard.app.dca.data.local.DCAExecutionEntity
import com.example.valguard.app.dca.data.local.DCAScheduleDao
import com.example.valguard.app.dca.domain.DCAFrequency
import com.example.valguard.app.dca.domain.DCASchedule
import com.example.valguard.app.dca.domain.NextExecutionCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class DCARepository(
    private val scheduleDao: DCAScheduleDao,
    private val executionDao: DCAExecutionDao
) {
    fun getAllSchedules(): Flow<List<DCASchedule>> {
        return scheduleDao.getAllSchedules().map { entities ->
            entities.map { DCASchedule.fromEntity(it) }
        }
    }
    
    fun getActiveSchedules(): Flow<List<DCASchedule>> {
        return scheduleDao.getActiveSchedules().map { entities ->
            entities.map { DCASchedule.fromEntity(it) }
        }
    }
    
    suspend fun getScheduleById(scheduleId: Long): DCASchedule? {
        return scheduleDao.getScheduleById(scheduleId)?.let { DCASchedule.fromEntity(it) }
    }
    
    suspend fun createSchedule(
        coinId: String,
        coinName: String,
        coinSymbol: String,
        coinIconUrl: String,
        amount: Double,
        frequency: DCAFrequency,
        dayOfWeek: Int? = null,
        dayOfMonth: Int? = null
    ): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        val nextExecution = NextExecutionCalculator.calculateNextExecutionDate(
            frequency = frequency,
            dayOfWeek = dayOfWeek,
            dayOfMonth = dayOfMonth,
            fromDate = now
        )
        
        val schedule = DCASchedule(
            coinId = coinId,
            coinName = coinName,
            coinSymbol = coinSymbol,
            coinIconUrl = coinIconUrl,
            amount = amount,
            frequency = frequency,
            dayOfWeek = dayOfWeek,
            dayOfMonth = dayOfMonth,
            isActive = true,
            createdAt = now,
            nextExecutionDate = nextExecution
        )
        
        return scheduleDao.insert(schedule.toEntity())
    }
    
    suspend fun updateSchedule(schedule: DCASchedule) {
        scheduleDao.update(schedule.toEntity())
    }
    
    suspend fun deleteSchedule(scheduleId: Long) {
        scheduleDao.deleteById(scheduleId)
    }
    
    suspend fun toggleScheduleActive(scheduleId: Long, isActive: Boolean) {
        scheduleDao.setScheduleActive(scheduleId, isActive)
    }
    
    suspend fun recordExecution(
        scheduleId: Long,
        amount: Double,
        coinPrice: Double
    ) {
        val schedule = scheduleDao.getScheduleById(scheduleId) ?: return
        
        // Record the execution
        val execution = DCAExecutionEntity(
            scheduleId = scheduleId,
            executedAt = Clock.System.now().toEpochMilliseconds(),
            amount = amount,
            coinPriceAtExecution = coinPrice,
            coinsAcquired = amount / coinPrice
        )
        executionDao.insert(execution)
        
        // Calculate next execution date
        val nextDate = NextExecutionCalculator.calculateNextExecutionDate(
            frequency = DCAFrequency.fromValue(schedule.frequency),
            dayOfWeek = schedule.dayOfWeek,
            dayOfMonth = schedule.dayOfMonth
        )
        
        // Update schedule
        scheduleDao.updateAfterExecution(scheduleId, nextDate, amount)
    }
    
    fun getExecutionsForSchedule(scheduleId: Long): Flow<List<DCAExecutionEntity>> {
        return executionDao.getExecutionsForSchedule(scheduleId)
    }
    
    suspend fun getTotalInvested(): Double {
        return scheduleDao.getTotalInvested() ?: 0.0
    }
    
    suspend fun getScheduleCount(): Int {
        return scheduleDao.getScheduleCount()
    }
}
