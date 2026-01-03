package com.example.valguard.app.dca.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DCAScheduleDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: DCAScheduleEntity): Long
    
    @Update
    suspend fun update(schedule: DCAScheduleEntity)
    
    @Delete
    suspend fun delete(schedule: DCAScheduleEntity)
    
    @Query("DELETE FROM dca_schedules WHERE id = :scheduleId")
    suspend fun deleteById(scheduleId: Long)
    
    @Query("SELECT * FROM dca_schedules ORDER BY createdAt DESC")
    fun getAllSchedules(): Flow<List<DCAScheduleEntity>>
    
    @Query("SELECT * FROM dca_schedules WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Long): DCAScheduleEntity?
    
    @Query("SELECT * FROM dca_schedules WHERE coinId = :coinId")
    suspend fun getSchedulesByCoinId(coinId: String): List<DCAScheduleEntity>
    
    @Query("SELECT * FROM dca_schedules WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveSchedules(): Flow<List<DCAScheduleEntity>>
    
    @Query("UPDATE dca_schedules SET isActive = :isActive WHERE id = :scheduleId")
    suspend fun setScheduleActive(scheduleId: Long, isActive: Boolean)
    
    @Query("UPDATE dca_schedules SET lastExecutedAt = :lastExecutedAt, totalInvested = totalInvested + :amount, executionCount = executionCount + 1 WHERE id = :scheduleId")
    suspend fun updateAfterExecution(scheduleId: Long, lastExecutedAt: Long, amount: Double)
    
    @Query("SELECT COUNT(*) FROM dca_schedules")
    suspend fun getScheduleCount(): Int
    
    @Query("SELECT SUM(totalInvested) FROM dca_schedules")
    suspend fun getTotalInvested(): Double?
}
