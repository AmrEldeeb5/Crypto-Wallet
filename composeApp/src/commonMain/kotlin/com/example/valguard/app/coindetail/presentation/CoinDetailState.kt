package com.example.valguard.app.coindetail.presentation

import com.example.valguard.app.coindetail.domain.ChartTimeframe
import com.example.valguard.app.coindetail.domain.CoinDetailData
import com.example.valguard.app.coindetail.domain.CoinHoldings
import com.example.valguard.app.components.ChartPoint
import com.example.valguard.app.core.util.UiState

data class CoinDetailState(
    val coinId: String = "",
    val coinData: UiState<CoinDetailData> = UiState.Loading.Initial,
    val chartData: Map<ChartTimeframe, UiState<List<ChartPoint>>> = emptyMap(),
    val holdings: CoinHoldings? = null,
    val selectedTimeframe: ChartTimeframe = ChartTimeframe.DAY_1,
    val isInWatchlist: Boolean = false,
    val isOffline: Boolean = false,
    val showAlertModal: Boolean = false
) {
    val hasHoldings: Boolean get() = holdings != null && holdings.amountOwned > 0
    
    val currentChartState: UiState<List<ChartPoint>>
        get() = chartData[selectedTimeframe] ?: UiState.Loading.Initial
}

sealed class CoinDetailEvent {
    data class LoadCoin(val coinId: String) : CoinDetailEvent()
    data class SelectTimeframe(val timeframe: ChartTimeframe) : CoinDetailEvent()
    data object ToggleWatchlist : CoinDetailEvent()
    data object Retry : CoinDetailEvent()
    data object ShowAlertModal : CoinDetailEvent()
    data object HideAlertModal : CoinDetailEvent()
    data object NavigateToBuy : CoinDetailEvent()
    data object NavigateToSell : CoinDetailEvent()
    data object NavigateBack : CoinDetailEvent()
}
