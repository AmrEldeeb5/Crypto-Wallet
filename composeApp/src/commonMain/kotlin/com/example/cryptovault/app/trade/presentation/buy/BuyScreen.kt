package com.example.cryptovault.app.trade.presentation.buy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptovault.app.trade.presentation.common.TradeEvent
import com.example.cryptovault.app.trade.presentation.common.TradeScreen
import com.example.cryptovault.app.trade.presentation.common.TradeType
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun BuyScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
) {
    val viewModel = koinViewModel<BuyViewModel> { parametersOf(coinId) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TradeEvent.NavigateToPortfolio -> navigateToPortfolio()
                is TradeEvent.ShowError -> { /* Error is shown in state */ }
            }
        }
    }

    TradeScreen(
        state = state,
        tradeType = TradeType.BUY,
        onAmountChange = viewModel::onAmountChanged,
        onSubmitClicked = viewModel::onSubmitClicked,
        onConfirmTrade = viewModel::onConfirmTrade,
        onCancelConfirmation = viewModel::onCancelConfirmation
    )
}