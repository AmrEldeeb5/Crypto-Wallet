package com.example.valguard.app.coindetail.domain

/**
 * Coin detail data - all market fields are nullable
 * If API doesn't provide data, UI should hide the field
 */
data class CoinDetailData(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String?,
    val price: Double,
    val change24h: Double,
    // Nullable fields - only show if API provides them
    val marketCapRank: Int? = null,
    val volume24h: Double? = null,
    val high24h: Double? = null,
    val low24h: Double? = null,
    val marketCap: Double? = null,
    val circulatingSupply: Double? = null,
    val allTimeHigh: Double? = null,
    val allTimeLow: Double? = null,
    val maxSupply: Double? = null,
    val description: String? = null,
    val priceHistory: List<PricePoint> = emptyList()
)

data class PricePoint(
    val timestamp: Long,
    val price: Double
)

data class CoinHoldings(
    val coinId: String,
    val amountOwned: Double,
    val averagePurchasePrice: Double,
    val currentValue: Double,
    val profitLoss: Double,
    val profitLossPercentage: Double
)

enum class ChartTimeframe(val value: String, val displayName: String, val apiPeriod: String) {
    DAY_1("24h", "24H", "24h"),
    WEEK_1("7d", "7D", "7d"),
    MONTH_1("30d", "1M", "30d"),
    MONTH_3("3m", "3M", "3m"),
    YEAR_1("1y", "1Y", "1y"),
    ALL("all", "ALL", "5y");
    
    companion object {
        fun fromValue(value: String): ChartTimeframe {
            return entries.find { it.value == value } ?: DAY_1
        }
    }
}
