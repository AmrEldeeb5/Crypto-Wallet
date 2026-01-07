package com.example.valguard.app.coins.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

/**
 * Room entity for cached coin market data from CoinGecko
 */
@Entity(tableName = "coins")
@TypeConverters(Converters::class)
data class CoinEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val image: String?,
    val currentPrice: Double?,
    val marketCap: Double?,
    val marketCapRank: Int?,
    val totalVolume: Double?,
    val high24h: Double?,
    val low24h: Double?,
    val priceChange24h: Double?,
    val priceChangePercentage24h: Double?,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double?,
    val atl: Double?,
    val sparkline7d: List<Double>?,
    val lastUpdated: Long  // Timestamp for cache invalidation
)

/**
 * Room entity for detailed coin information (description, etc.)
 */
@Entity(tableName = "coin_details")
data class CoinDetailEntity(
    @PrimaryKey val id: String,
    val description: String?,
    val lastUpdated: Long
)

/**
 * TypeConverters for Room database
 */
class Converters {
    @TypeConverter
    fun fromDoubleList(value: List<Double>?): String? {
        return value?.joinToString(",")
    }
    
    @TypeConverter
    fun toDoubleList(value: String?): List<Double>? {
        if (value.isNullOrBlank()) return null
        return value.split(",").mapNotNull { it.toDoubleOrNull() }
    }
}
