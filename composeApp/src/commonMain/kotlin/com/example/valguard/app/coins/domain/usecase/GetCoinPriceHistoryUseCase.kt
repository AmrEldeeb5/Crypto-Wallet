package com.example.valguard.app.coins.domain.usecase

import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.coins.domain.model.PriceModel
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.core.domain.Result

class GetCoinPriceHistoryUseCase(
    private val repository: CoinGeckoRepository,
) {
    /**
     * Get price history for charts
     * Maps timeframe to CoinGecko days parameter
     */
    suspend fun execute(coinId: String, timePeriod: String): Result<List<PriceModel>, DataError.Remote> {
        val days = mapTimePeriodToDays(timePeriod)
        return repository.getPriceHistory(coinId, days)
    }

    private fun mapTimePeriodToDays(timePeriod: String): String {
        return when (timePeriod) {
            "1h" -> "1"
            "24h", "1d" -> "1"
            "7d" -> "7"
            "30d", "1m" -> "30"
            "3m" -> "90"
            "1y" -> "365"
            "all" -> "max"
            else -> "7"
        }
    }
}