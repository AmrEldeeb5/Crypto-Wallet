/**
 * UiCoinItem.kt
 *
 * UI model representing a cryptocurrency for display in lists and cards.
 * Contains pre-formatted strings for direct use in UI components.
 *
 * This is a presentation-layer model that separates UI concerns from
 * domain models. Values are pre-formatted for display efficiency.
 *
 * @see CoinCard for the primary display component
 * @see ExpandableCoinCard for the expandable variant
 */
package com.example.valguard.app.components

import com.example.valguard.app.realtime.domain.PriceDirection

/**
 * UI model for displaying cryptocurrency information.
 *
 * Contains all data needed to render a coin in list views,
 * with values pre-formatted for display.
 *
 * @property id Unique identifier for the coin
 * @property name Full name of the cryptocurrency (e.g., "Bitcoin")
 * @property symbol Trading symbol (e.g., "BTC")
 * @property iconUrl URL to the coin's icon image
 * @property formattedPrice Pre-formatted price string (e.g., "$45,123.45")
 * @property formattedChange Pre-formatted change percentage (e.g., "+5.23%")
 * @property changePercent Raw percentage change value (e.g., 5.23, -2.40)
 * @property isPositive Whether the price change is positive
 * @property priceDirection Real-time price movement direction
 * @property sparklineData Optional sparkline chart data points
 * @property holdingsAmount Optional formatted holdings amount (e.g., "0.5 BTC")
 * @property holdingsValue Optional formatted holdings value (e.g., "$22,561.73")
 * @property marketCap Optional formatted market cap (e.g., "$850B")
 * @property volume24h Optional formatted 24h volume
 * @property isInWatchlist Whether the coin is in user's watchlist
 */
data class UiCoinItem(
    val id: String,
    val name: String,
    val symbol: String,
    val iconUrl: String,
    val formattedPrice: String,
    val formattedChange: String,
    val changePercent: Double,
    val isPositive: Boolean,
    val priceDirection: PriceDirection,
    val sparklineData: List<Double> = emptyList(),
    val holdingsAmount: String? = null,
    val holdingsValue: String? = null,
    val marketCap: String? = null,
    val volume24h: String? = null,
    val isInWatchlist: Boolean = false
) {
    /**
     * Checks if the user has holdings of this coin.
     *
     * @return True if both holdingsAmount and holdingsValue are set
     */
    fun hasHoldings(): Boolean = holdingsAmount != null && holdingsValue != null
}
