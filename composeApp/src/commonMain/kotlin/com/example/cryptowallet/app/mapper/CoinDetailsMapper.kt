package com.example.cryptowallet.app.mapper

import com.example.cryptowallet.app.coins.data.remote.dto.CoinDetailsDto
import com.example.cryptowallet.app.core.domain.coin.Coin
import com.example.cryptowallet.app.coins.domain.model.CoinModel

fun CoinDetailsDto.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price,
    change = change,
)

