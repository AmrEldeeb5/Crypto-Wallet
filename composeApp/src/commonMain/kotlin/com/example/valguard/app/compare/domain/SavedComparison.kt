package com.example.valguard.app.compare.domain

import com.example.valguard.app.compare.data.local.SavedComparisonEntity

data class SavedComparison(
    val id: Long = 0,
    val coin1Id: String,
    val coin1Name: String,
    val coin1Symbol: String,
    val coin1IconUrl: String,
    val coin2Id: String,
    val coin2Name: String,
    val coin2Symbol: String,
    val coin2IconUrl: String,
    val savedAt: Long
) {
    fun toEntity(): SavedComparisonEntity = SavedComparisonEntity(
        id = id,
        coin1Id = coin1Id,
        coin1Name = coin1Name,
        coin1Symbol = coin1Symbol,
        coin1IconUrl = coin1IconUrl,
        coin2Id = coin2Id,
        coin2Name = coin2Name,
        coin2Symbol = coin2Symbol,
        coin2IconUrl = coin2IconUrl,
        savedAt = savedAt
    )
    
    companion object {
        fun fromEntity(entity: SavedComparisonEntity): SavedComparison = SavedComparison(
            id = entity.id,
            coin1Id = entity.coin1Id,
            coin1Name = entity.coin1Name,
            coin1Symbol = entity.coin1Symbol,
            coin1IconUrl = entity.coin1IconUrl,
            coin2Id = entity.coin2Id,
            coin2Name = entity.coin2Name,
            coin2Symbol = entity.coin2Symbol,
            coin2IconUrl = entity.coin2IconUrl,
            savedAt = entity.savedAt
        )
    }
}
