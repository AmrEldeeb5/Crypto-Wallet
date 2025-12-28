package com.example.cryptovault.app.realtime.domain

object PortfolioValueCalculator {


    data class CoinHolding(
        val coinId: String,
        val amountOwned: Double
    )


    fun calculateTotalValue(
        holdings: List<CoinHolding>,
        prices: Map<String, Double>
    ): Double {
        return holdings.sumOf { holding ->
            val price = prices[holding.coinId] ?: 0.0
            holding.amountOwned * price
        }
    }

    fun updateValueOnPriceChange(
        currentValue: Double,
        holding: CoinHolding,
        oldPrice: Double,
        newPrice: Double
    ): Double {
        val oldContribution = holding.amountOwned * oldPrice
        val newContribution = holding.amountOwned * newPrice
        return currentValue - oldContribution + newContribution
    }
}
