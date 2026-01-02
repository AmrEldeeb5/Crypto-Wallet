package com.example.valguard.app.dca.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dca_schedules")
data class DCAScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coinId: String,
    val coinName: String,
    val coinSymbol: String,
    val coinIconUrl: String,
    val amount: Double,
    val frequency: String, // "daily", "weekly", "biweekly", "monthly"
    val dayOfWeek: Int? = null, // 1-7 for weekly/biweekly (Monday=1)
    val dayOfMonth: Int? = null, // 1-28 for monthly
    val isActive: Boolean = true,
    val createdAt: Long,
    val nextExecutionDate: Long,
    val totalInvested: Double = 0.0,
    val executionCount: Int = 0
)
