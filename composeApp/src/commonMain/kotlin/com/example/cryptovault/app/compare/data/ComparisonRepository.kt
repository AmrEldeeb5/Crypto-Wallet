package com.example.cryptovault.app.compare.data

import com.example.cryptovault.app.compare.data.local.SavedComparisonDao
import com.example.cryptovault.app.compare.domain.SavedComparison
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class ComparisonRepository(
    private val savedComparisonDao: SavedComparisonDao
) {
    fun getAllComparisons(): Flow<List<SavedComparison>> {
        return savedComparisonDao.getAllComparisons().map { entities ->
            entities.map { SavedComparison.fromEntity(it) }
        }
    }
    
    suspend fun getRecentComparisons(limit: Int = 5): List<SavedComparison> {
        return savedComparisonDao.getRecentComparisons(limit).map { SavedComparison.fromEntity(it) }
    }
    
    suspend fun getComparisonById(comparisonId: Long): SavedComparison? {
        return savedComparisonDao.getComparisonById(comparisonId)?.let { SavedComparison.fromEntity(it) }
    }
    
    suspend fun saveComparison(
        coin1Id: String,
        coin1Name: String,
        coin1Symbol: String,
        coin1IconUrl: String,
        coin2Id: String,
        coin2Name: String,
        coin2Symbol: String,
        coin2IconUrl: String
    ): Long {
        val comparison = SavedComparison(
            coin1Id = coin1Id,
            coin1Name = coin1Name,
            coin1Symbol = coin1Symbol,
            coin1IconUrl = coin1IconUrl,
            coin2Id = coin2Id,
            coin2Name = coin2Name,
            coin2Symbol = coin2Symbol,
            coin2IconUrl = coin2IconUrl,
            savedAt = Clock.System.now().toEpochMilliseconds()
        )
        
        return savedComparisonDao.insert(comparison.toEntity())
    }
    
    suspend fun deleteComparison(comparisonId: Long) {
        savedComparisonDao.deleteById(comparisonId)
    }
    
    suspend fun comparisonExists(coinId1: String, coinId2: String): Boolean {
        return savedComparisonDao.findComparison(coinId1, coinId2) != null
    }
    
    suspend fun getComparisonCount(): Int {
        return savedComparisonDao.getComparisonCount()
    }
}
