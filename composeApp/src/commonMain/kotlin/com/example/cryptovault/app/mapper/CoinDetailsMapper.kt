package com.example.cryptovault.app.mapper

import com.example.cryptovault.app.coins.data.remote.dto.CoinDetailsDto
import com.example.cryptovault.app.core.domain.coin.Coin
import com.example.cryptovault.app.coins.domain.model.CoinModel

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

