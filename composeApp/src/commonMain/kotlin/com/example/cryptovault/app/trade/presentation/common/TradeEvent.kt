package com.example.cryptovault.app.trade.presentation.common

import org.jetbrains.compose.resources.StringResource

sealed class TradeEvent {
    data object NavigateToPortfolio : TradeEvent()
    data class ShowError(val message: StringResource) : TradeEvent()
}
