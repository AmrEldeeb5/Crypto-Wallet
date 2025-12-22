package com.example.cryptowallet.app.portfolio.data.mapper

import com.example.cryptowallet.app.core.domain.coin.Coin
import com.example.cryptowallet.app.portfolio.data.local.PortfolioCoinEntity
import com.example.cryptowallet.app.portfolio.domain.PortfolioCoinModel
import kotlinx.datetime.Clock

// Extension function to convert PortfolioCoinEntity to PortfolioCoinModel

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double,
): PortfolioCoinModel {
    return PortfolioCoinModel(
        coin = Coin(
            id = coinId,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl
        ),
        performancePercent = ((currentPrice - averagePurchasePrice) / averagePurchasePrice) * 100,
        averagePurchasePrice = averagePurchasePrice,
        ownedAmountInUnit = amountOwned,
        ownedAmountInFiat = amountOwned * currentPrice
    )
}

fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity {
    return PortfolioCoinEntity(
        coinId = coin.id,
        name = coin.name,
        symbol = coin.symbol,
        iconUrl = coin.iconUrl,
        amountOwned = ownedAmountInUnit,
        averagePurchasePrice = averagePurchasePrice,
        timestamp = Clock.System.now().toEpochMilliseconds()
    )
}