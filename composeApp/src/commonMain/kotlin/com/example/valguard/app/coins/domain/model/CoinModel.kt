package com.example.valguard.app.coins.domain.model

import com.example.valguard.app.core.domain.coin.Coin


data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double,
)
