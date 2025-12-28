package com.example.cryptovault.app.coins.domain.model

import com.example.cryptovault.app.core.domain.coin.Coin


data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double,
)
