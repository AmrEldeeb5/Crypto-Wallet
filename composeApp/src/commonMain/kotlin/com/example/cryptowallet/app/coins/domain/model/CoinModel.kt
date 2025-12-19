package com.example.cryptowallet.app.coins.domain.model

import com.example.cryptowallet.app.core.domain.coin.Coin


data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double,
)
