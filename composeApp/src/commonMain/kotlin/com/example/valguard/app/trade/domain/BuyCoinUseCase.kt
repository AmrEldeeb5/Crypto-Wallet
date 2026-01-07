package com.example.valguard.app.trade.domain

import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.EmptyResult
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.domain.coin.Coin
import com.example.valguard.app.portfolio.domain.PortfolioCoinModel
import com.example.valguard.app.portfolio.domain.PortfolioRepository
import kotlinx.coroutines.flow.first

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {

    suspend fun buyCoin(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.cashBalanceFlow().first()
        if (balance < amountInFiat) {
            return Result.Failure(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val existingCoinResult = portfolioRepository.getPortfolioCoin(coin.id)
        val existingCoin = when (existingCoinResult) {
            is Result.Success -> existingCoinResult.data
            is Result.Failure -> return Result.Failure(existingCoinResult.error)
        }
        val amountInUnit = amountInFiat / price
        if (existingCoin != null) {
            val newAmountOwned = existingCoin.ownedAmountInUnit + amountInUnit
            val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInFiat
            val newAveragePurchasePrice = newTotalInvestment / newAmountOwned
            portfolioRepository.savePortfolioCoin(
                existingCoin.copy(
                    ownedAmountInUnit = newAmountOwned,
                    ownedAmountInFiat = newTotalInvestment,
                    averagePurchasePrice = newAveragePurchasePrice
                )
            )
        } else {
            portfolioRepository.savePortfolioCoin(
                PortfolioCoinModel(
                    coin = coin,
                    performancePercent = 0.0,
                    priceChangePercentage24h = null, // Will be populated from CoinGecko cache
                    averagePurchasePrice = price,
                    ownedAmountInFiat = amountInFiat,
                    ownedAmountInUnit = amountInUnit
                )
            )
        }
        portfolioRepository.updateCashBalance(balance - amountInFiat)
        return Result.Success(Unit)
    }
}