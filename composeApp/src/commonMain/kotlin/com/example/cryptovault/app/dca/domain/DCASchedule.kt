package com.example.cryptovault.app.dca.domain

import com.example.cryptovault.app.dca.data.local.DCAScheduleEntity

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
    val nextExecutionDate: Long,
    val totalInvested: Double = 0.0,
    val executionCount: Int = 0
) {
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
        nextExecutionDate = nextExecutionDate,
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
            nextExecutionDate = entity.nextExecutionDate,
            totalInvested = entity.totalInvested,
            executionCount = entity.executionCount
        )
    }
}
