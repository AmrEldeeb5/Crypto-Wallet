package com.example.valguard.app.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinResponseDto(
    val data: CoinsListDto
)

@Serializable
data class CoinsListDto(
    val coins: List<CoinItemDto>
)

@Serializable
data class CoinItemDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String? = null,
    val price: String? = null,
    val rank: Int,
    val change: String? = null,
    val sparkline: List<String>? = null,
    val marketCap: String? = null
)
