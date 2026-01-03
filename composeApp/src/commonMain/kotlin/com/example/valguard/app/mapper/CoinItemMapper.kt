package com.example.valguard.app.mapper

import com.example.valguard.app.coins.data.remote.dto.CoinItemDto
import com.example.valguard.app.coins.data.remote.dto.CoinPriceHistoryDto
import com.example.valguard.app.core.domain.coin.Coin
import com.example.valguard.app.coins.domain.model.CoinModel
import com.example.valguard.app.coins.domain.model.PriceModel


fun CoinItemDto.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl ?: "",
    ),
    price = price?.toDoubleOrNull() ?: 0.0,
    change = change?.toDoubleOrNull() ?: 0.0,
    sparkline = sparkline?.mapNotNull { it.toDoubleOrNull() } ?: emptyList(),
    marketCap = marketCap?.toDoubleOrNull() ?: 0.0
)

fun CoinPriceHistoryDto.toPriceModel() = PriceModel(
    price = price?.toDoubleOrNull() ?: 0.0,
    timestamp = this.timestamp
)