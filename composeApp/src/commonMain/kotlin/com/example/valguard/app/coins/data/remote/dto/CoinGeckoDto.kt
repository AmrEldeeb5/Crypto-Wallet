package com.example.valguard.app.coins.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * CoinGecko API DTOs
 * Single source of truth for all cryptocurrency market data
 */

// /coins/markets response
@Serializable
data class CoinGeckoMarketDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String? = null,
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("market_cap") val marketCap: Double? = null,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    @SerialName("total_volume") val totalVolume: Double? = null,
    @SerialName("high_24h") val high24h: Double? = null,
    @SerialName("low_24h") val low24h: Double? = null,
    @SerialName("price_change_24h") val priceChange24h: Double? = null,
    @SerialName("price_change_percentage_24h") val priceChangePercentage24h: Double? = null,
    @SerialName("circulating_supply") val circulatingSupply: Double? = null,
    @SerialName("total_supply") val totalSupply: Double? = null,
    @SerialName("max_supply") val maxSupply: Double? = null,
    val ath: Double? = null,
    val atl: Double? = null,
    @SerialName("sparkline_in_7d") val sparklineIn7d: SparklineDto? = null
)

@Serializable
data class SparklineDto(
    val price: List<Double>? = null
)

// /coins/{id} response
@Serializable
data class CoinGeckoDetailDto(
    val id: String,
    val symbol: String,
    val name: String,
    val description: DescriptionDto? = null,
    val image: ImageDto? = null,
    @SerialName("market_cap_rank") val marketCapRank: Int? = null,
    @SerialName("market_data") val marketData: MarketDataDto? = null
)

@Serializable
data class DescriptionDto(
    val en: String? = null
)

@Serializable
data class ImageDto(
    val large: String? = null,
    val small: String? = null,
    val thumb: String? = null
)

@Serializable
data class MarketDataDto(
    @SerialName("current_price") val currentPrice: Map<String, Double>? = null,
    @SerialName("market_cap") val marketCap: Map<String, Double>? = null,
    @SerialName("total_volume") val totalVolume: Map<String, Double>? = null,
    @SerialName("high_24h") val high24h: Map<String, Double>? = null,
    @SerialName("low_24h") val low24h: Map<String, Double>? = null,
    @SerialName("price_change_percentage_24h") val priceChangePercentage24h: Double? = null,
    @SerialName("circulating_supply") val circulatingSupply: Double? = null,
    @SerialName("total_supply") val totalSupply: Double? = null,
    @SerialName("max_supply") val maxSupply: Double? = null,
    val ath: Map<String, Double>? = null,
    val atl: Map<String, Double>? = null
)

// /coins/{id}/market_chart response
@Serializable
data class CoinGeckoChartDto(
    val prices: List<List<Double>> = emptyList()  // [[timestamp, price], ...]
)
