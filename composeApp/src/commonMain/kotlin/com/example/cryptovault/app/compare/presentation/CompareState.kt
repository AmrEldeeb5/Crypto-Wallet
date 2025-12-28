package com.example.cryptovault.app.compare.presentation

import com.example.cryptovault.app.compare.domain.SavedComparison
import com.example.cryptovault.app.core.util.UiState

data class CompareState(
    val coin1: CoinSlot? = null,
    val coin2: CoinSlot? = null,
    val comparisonData: ComparisonData? = null,
    val savedComparisons: UiState<List<SavedComparison>> = UiState.Loading,
    val showCoinSelector: CoinSelectorTarget? = null,
    val coinSearchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CoinSlot(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val price: Double = 0.0,
    val change24h: Double = 0.0,
    val marketCap: Double = 0.0,
    val volume24h: Double = 0.0,
    val marketCapRank: Int = 0,
    val circulatingSupply: Double = 0.0
)

enum class CoinSelectorTarget {
    COIN_1,
    COIN_2
}

data class ComparisonData(
    val priceWinner: MetricWinner,
    val change24hWinner: MetricWinner,
    val marketCapWinner: MetricWinner,
    val volumeWinner: MetricWinner,
    val rankWinner: MetricWinner,
    val priceDifference: Double,
    val change24hDifference: Double,
    val marketCapDifference: Double,
    val volumeDifference: Double
)

enum class MetricWinner {
    COIN_1,
    COIN_2,
    TIE
}

sealed class CompareEvent {
    data object LoadSavedComparisons : CompareEvent()
    data class ShowCoinSelector(val target: CoinSelectorTarget) : CompareEvent()
    data object HideCoinSelector : CompareEvent()
    data class UpdateSearchQuery(val query: String) : CompareEvent()
    data class SelectCoin(val coinId: String, val name: String, val symbol: String, val iconUrl: String) : CompareEvent()
    data object SwapCoins : CompareEvent()
    data object SaveComparison : CompareEvent()
    data class LoadSavedComparison(val comparison: SavedComparison) : CompareEvent()
    data class DeleteSavedComparison(val comparisonId: Long) : CompareEvent()
    data object ClearComparison : CompareEvent()
    data object Retry : CompareEvent()
}

object ComparisonCalculator {
    
    fun calculateComparison(coin1: CoinSlot, coin2: CoinSlot): ComparisonData {
        return ComparisonData(
            priceWinner = determineWinner(coin1.price, coin2.price, higherIsBetter = true),
            change24hWinner = determineWinner(coin1.change24h, coin2.change24h, higherIsBetter = true),
            marketCapWinner = determineWinner(coin1.marketCap, coin2.marketCap, higherIsBetter = true),
            volumeWinner = determineWinner(coin1.volume24h, coin2.volume24h, higherIsBetter = true),
            rankWinner = determineWinner(coin1.marketCapRank.toDouble(), coin2.marketCapRank.toDouble(), higherIsBetter = false),
            priceDifference = calculatePercentageDifference(coin1.price, coin2.price),
            change24hDifference = calculateAbsoluteDifference(coin1.change24h, coin2.change24h),
            marketCapDifference = calculatePercentageDifference(coin1.marketCap, coin2.marketCap),
            volumeDifference = calculatePercentageDifference(coin1.volume24h, coin2.volume24h)
        )
    }
    
    fun determineWinner(value1: Double, value2: Double, higherIsBetter: Boolean): MetricWinner {
        return when {
            value1 == value2 -> MetricWinner.TIE
            higherIsBetter -> if (value1 > value2) MetricWinner.COIN_1 else MetricWinner.COIN_2
            else -> if (value1 < value2) MetricWinner.COIN_1 else MetricWinner.COIN_2
        }
    }
    
    fun calculatePercentageDifference(value1: Double, value2: Double): Double {
        if (value2 == 0.0) return if (value1 == 0.0) 0.0 else 100.0
        return ((value1 - value2) / value2) * 100
    }
    
    fun calculateAbsoluteDifference(value1: Double, value2: Double): Double {
        return value1 - value2
    }
}
