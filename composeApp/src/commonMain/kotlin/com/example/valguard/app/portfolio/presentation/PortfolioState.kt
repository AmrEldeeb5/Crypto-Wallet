package com.example.valguard.app.portfolio.presentation

import com.example.valguard.app.realtime.domain.ConnectionState
import org.jetbrains.compose.resources.StringResource

data class PortfolioState(
    val portfolioValue: String = "",
    val cashBalance: String = "",
    val performancePercent: String = "0.0%",
    val isPerformancePositive: Boolean = false,
    val showBuyButton: Boolean = false,
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val coins: List<UiPortfolioCoinItem> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED
)