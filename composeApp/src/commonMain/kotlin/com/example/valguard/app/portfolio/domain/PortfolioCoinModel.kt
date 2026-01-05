package com.example.valguard.app.portfolio.domain

import com.example.valguard.app.core.domain.coin.Coin


data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double, // User's portfolio performance (purchase price vs current)
    val priceChangePercentage24h: Double?, // 24h market change from CoinGecko
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double,
)