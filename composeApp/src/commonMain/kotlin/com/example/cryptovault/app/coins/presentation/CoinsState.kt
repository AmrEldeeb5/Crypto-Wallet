package com.example.cryptovault.app.coins.presentation

import androidx.compose.runtime.Stable
import com.example.cryptovault.app.realtime.domain.ConnectionState
import com.example.cryptovault.app.realtime.domain.PriceDirection
import org.jetbrains.compose.resources.StringResource


@Stable
data class CoinsState(
    val isLoading: Boolean = true,
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
    val chartState: UiChartState? = null,
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

@Stable
data class UiChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
    val error: String? = null
)