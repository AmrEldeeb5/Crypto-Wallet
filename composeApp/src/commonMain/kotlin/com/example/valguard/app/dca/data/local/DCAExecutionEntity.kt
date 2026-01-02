package com.example.valguard.app.dca.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dca_executions",
    foreignKeys = [
        ForeignKey(
            entity = DCAScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scheduleId")]
)
data class DCAExecutionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scheduleId: Long,
    val executedAt: Long,
    val amount: Double,
    val coinPriceAtExecution: Double,
    val coinsAcquired: Double
)
