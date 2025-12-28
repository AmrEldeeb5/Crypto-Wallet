package com.example.cryptovault.app.coins.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class CoinPriceHistoryResponseDto(
    val data: CoinPriceHistoryDataDto
)

@Serializable
data class CoinPriceHistoryDataDto(
    val history: List<CoinPriceHistoryDto>
)

@Serializable
data class CoinPriceHistoryDto(
    val price: Double,
    val timestamp: Long
)