package com.example.cryptovault.app.compare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_comparisons")
data class SavedComparisonEntity(
    @PrimaryKey(autoGenerate = true)
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
)
