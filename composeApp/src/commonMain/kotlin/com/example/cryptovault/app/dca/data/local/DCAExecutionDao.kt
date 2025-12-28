package com.example.cryptovault.app.dca.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DCAExecutionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(execution: DCAExecutionEntity): Long
    
    @Query("SELECT * FROM dca_executions WHERE scheduleId = :scheduleId ORDER BY executedAt DESC")
    fun getExecutionsForSchedule(scheduleId: Long): Flow<List<DCAExecutionEntity>>
    
    @Query("SELECT * FROM dca_executions ORDER BY executedAt DESC LIMIT :limit")
    suspend fun getRecentExecutions(limit: Int): List<DCAExecutionEntity>
    
    @Query("SELECT * FROM dca_executions WHERE executedAt >= :startTime AND executedAt <= :endTime ORDER BY executedAt DESC")
    suspend fun getExecutionsInRange(startTime: Long, endTime: Long): List<DCAExecutionEntity>
    
    @Query("SELECT SUM(amount) FROM dca_executions WHERE scheduleId = :scheduleId")
    suspend fun getTotalInvestedForSchedule(scheduleId: Long): Double?
    
    @Query("SELECT COUNT(*) FROM dca_executions WHERE scheduleId = :scheduleId")
    suspend fun getExecutionCountForSchedule(scheduleId: Long): Int
    
    @Query("DELETE FROM dca_executions WHERE scheduleId = :scheduleId")
    suspend fun deleteExecutionsForSchedule(scheduleId: Long)
}
