package com.example.cryptovault.app.trade.presentation.common

import com.example.cryptovault.app.realtime.domain.ConnectionState
import org.jetbrains.compose.resources.StringResource

data class TradeState(
    val isLoading: Boolean = false,
    val error: StringResource? = null,
    val availableAmount: String = "",
    val availableAmountValue: Double = 0.0,
    val amount: String = "",
    val coin: UiTradeCoinItem? = null,
    val connectionState: ConnectionState = ConnectionState.DISCONNECTED,
    val showConfirmation: Boolean = false,
    val isExecuting: Boolean = false,
    val validationError: StringResource? = null,
    val isAmountValid: Boolean = false
)