package com.example.valguard.app.dca.domain

import com.example.valguard.app.dca.data.local.DCAScheduleEntity
import kotlinx.datetime.Clock

data class DCASchedule(
    val id: Long = 0,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val amount: Double,
    val frequency: DCAFrequency,
    val dayOfWeek: Int? = null,
    val dayOfMonth: Int? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val lastExecutedAt: Long? = null,
    val totalInvested: Double = 0.0,
    val executionCount: Int = 0
) {
    // Computed property - not stored in database
    fun nextExecution(now: Long = Clock.System.now().toEpochMilliseconds()): Long {
        return NextExecutionCalculator.calculateNextExecutionDate(
            frequency = frequency,
            dayOfWeek = dayOfWeek,
            dayOfMonth = dayOfMonth,
            fromDate = lastExecutedAt ?: createdAt
        )
    }
    
    fun toEntity(): DCAScheduleEntity = DCAScheduleEntity(
        id = id,
        coinId = coinId,
        coinName = coinName,
        coinSymbol = coinSymbol,
        coinIconUrl = coinIconUrl,
        amount = amount,
        frequency = frequency.value,
        dayOfWeek = dayOfWeek,
        dayOfMonth = dayOfMonth,
        isActive = isActive,
        createdAt = createdAt,
        lastExecutedAt = lastExecutedAt,
        totalInvested = totalInvested,
        executionCount = executionCount
    )
    
    companion object {
        fun fromEntity(entity: DCAScheduleEntity): DCASchedule = DCASchedule(
            id = entity.id,
            coinId = entity.coinId,
            coinName = entity.coinName,
            coinSymbol = entity.coinSymbol,
            coinIconUrl = entity.coinIconUrl,
            amount = entity.amount,
            frequency = DCAFrequency.fromValue(entity.frequency),
            dayOfWeek = entity.dayOfWeek,
            dayOfMonth = entity.dayOfMonth,
            isActive = entity.isActive,
            createdAt = entity.createdAt,
            lastExecutedAt = entity.lastExecutedAt,
            totalInvested = entity.totalInvested,
            executionCount = entity.executionCount
        )
    }
}
