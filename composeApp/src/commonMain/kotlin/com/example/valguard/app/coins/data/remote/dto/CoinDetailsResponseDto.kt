package com.example.valguard.app.coins.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class CoinDetailsResponseDto(
    val data: CoinDetailsDataDto
)

@Serializable
data class CoinDetailsDataDto(
    val coin: CoinDetailsDto  // Single object, not a list
)

@Serializable
data class CoinDetailsDto(
    val uuid: String,
    val symbol: String,
    val name: String,
    val iconUrl: String,
    val price: String,  // API returns price as String
    val rank: Int,
    val change: String  // API returns change as String
)

