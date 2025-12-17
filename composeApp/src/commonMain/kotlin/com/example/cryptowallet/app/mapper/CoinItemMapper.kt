package com.example.cryptowallet.app.mapper

import com.example.cryptowallet.app.coins.data.remote.dto.CoinItemDto
import com.example.cryptowallet.app.coins.data.remote.dto.CoinPriceHistoryDto
import com.example.cryptowallet.app.coins.domain.coin.Coin
import com.example.cryptowallet.app.coins.domain.model.CoinModel
import com.example.cryptowallet.app.coins.domain.model.PriceModel


fun CoinItemDto.toCoinModel() = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price,
    change = change,
)

fun CoinPriceHistoryDto.toPriceModel() = PriceModel(
    price = price,
    timestamp = this.timestamp
)