package com.example.cryptovault.app.mapper

import com.example.cryptovault.app.coins.data.remote.dto.CoinItemDto
import com.example.cryptovault.app.coins.data.remote.dto.CoinPriceHistoryDto
import com.example.cryptovault.app.core.domain.coin.Coin
import com.example.cryptovault.app.coins.domain.model.CoinModel
import com.example.cryptovault.app.coins.domain.model.PriceModel


fun CoinItemDto.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl ?: "",
    ),
    price = price?.toDoubleOrNull() ?: 0.0,
    change = change?.toDoubleOrNull() ?: 0.0,
)

fun CoinPriceHistoryDto.toPriceModel() = PriceModel(
    price = price,
    timestamp = this.timestamp
)