package com.example.valguard.app.mapper

import com.example.valguard.app.coins.data.remote.dto.CoinDetailsDto
import com.example.valguard.app.core.domain.coin.Coin
import com.example.valguard.app.coins.domain.model.CoinModel

fun CoinDetailsDto.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price.toDoubleOrNull() ?: 0.0,
    change = change.toDoubleOrNull() ?: 0.0,
)

