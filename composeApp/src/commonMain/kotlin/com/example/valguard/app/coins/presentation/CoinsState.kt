package com.example.valguard.app.coins.presentation

import androidx.compose.runtime.Stable
import com.example.valguard.app.realtime.domain.ConnectionState
import com.example.valguard.app.realtime.domain.PriceDirection
import org.jetbrains.compose.resources.StringResource


@Stable
data class CoinsState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
    val chartState: ChartState? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val searchQuery: String = ""
) {
    val filteredCoins: List<UiCoinListItem>
        get() = if (searchQuery.isBlank()) {
            coins
        } else {
            coins.filter { coin ->
                coin.name.contains(searchQuery, ignoreCase = true) ||
                coin.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
}

/**
 * Explicit state machine for chart loading
 * Prevents race conditions and ambiguous UI states
 */
@Stable
sealed class ChartState {
    abstract val coinName: String
    abstract val coinSymbol: String
    abstract val changePercent: Double
    
    data class Loading(
        override val coinName: String,
        override val coinSymbol: String,
        override val changePercent: Double
    ) : ChartState()
    
    data class Success(
        val sparkLine: List<Double>,
        override val coinName: String,
        override val coinSymbol: String,
        override val changePercent: Double
    ) : ChartState()
    
    data class Error(
        val message: String,
        override val coinName: String,
        override val coinSymbol: String,
        override val changePercent: Double
    ) : ChartState()
    
    data class Empty(
        override val coinName: String,
        override val coinSymbol: String,
        override val changePercent: Double
    ) : ChartState()
}

// Legacy support - will be removed
@Deprecated("Use ChartState sealed class instead")
@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
    val coinSymbol: String = "",
    val changePercent: Double = 0.0,
    val error: String? = null
)