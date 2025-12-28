package com.example.cryptovault.app.compare.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedComparisonDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comparison: SavedComparisonEntity): Long
    
    @Delete
    suspend fun delete(comparison: SavedComparisonEntity)
    
    @Query("DELETE FROM saved_comparisons WHERE id = :comparisonId")
    suspend fun deleteById(comparisonId: Long)
    
    @Query("SELECT * FROM saved_comparisons ORDER BY savedAt DESC")
    fun getAllComparisons(): Flow<List<SavedComparisonEntity>>
    
    @Query("SELECT * FROM saved_comparisons ORDER BY savedAt DESC LIMIT :limit")
    suspend fun getRecentComparisons(limit: Int): List<SavedComparisonEntity>
    
    @Query("SELECT * FROM saved_comparisons WHERE id = :comparisonId")
    suspend fun getComparisonById(comparisonId: Long): SavedComparisonEntity?
    
    @Query("SELECT * FROM saved_comparisons WHERE (coin1Id = :coinId1 AND coin2Id = :coinId2) OR (coin1Id = :coinId2 AND coin2Id = :coinId1)")
    suspend fun findComparison(coinId1: String, coinId2: String): SavedComparisonEntity?
    
    @Query("SELECT COUNT(*) FROM saved_comparisons")
    suspend fun getComparisonCount(): Int
}
