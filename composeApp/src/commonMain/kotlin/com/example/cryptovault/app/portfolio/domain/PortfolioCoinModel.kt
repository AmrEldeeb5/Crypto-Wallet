package com.example.cryptovault.app.portfolio.domain

import com.example.cryptovault.app.core.domain.coin.Coin


data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double,
)