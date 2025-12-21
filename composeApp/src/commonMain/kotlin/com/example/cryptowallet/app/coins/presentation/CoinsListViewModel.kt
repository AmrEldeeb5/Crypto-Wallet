package com.example.cryptowallet.app.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptowallet.app.coins.domain.GetCoinPriceHistoryUseCase
import com.example.cryptowallet.app.coins.domain.GetCoinsListUseCase
import com.example.cryptowallet.app.core.domain.Result
import com.example.cryptowallet.app.core.util.formatFiat
import com.example.cryptowallet.app.core.util.toUiText
import cryptowallet.composeapp.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart {
            viewModelScope.launch {
                getAllCoins()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState()
        )

    private suspend fun getAllCoins() {
        _state.update { it.copy(isLoading = true, error = null) }

        when (val coinsResponse = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                _state.update {
                    CoinsState(
                        isLoading = false,
                        coins = coinsResponse.data.map { coinItem ->
                            UiCoinListItem(
                                id = coinItem.coin.id,
                                name = coinItem.coin.name,
                                iconUrl = coinItem.coin.iconUrl,
                                symbol = coinItem.coin.symbol,
                                formattedPrice = formatFiat(coinItem.price),
                                formattedChange = formatFiat(coinItem.change, showDecimal = false),
                                isPositive = coinItem.change >= 0,
                            )
                        }
                    )
                }
            }

            is Result.Failure -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        coins = emptyList(),
                        error = coinsResponse.error.toUiText()
                    )
                }
            }
        }

        fun onCoinLongPressed(coinId: String) {
            _state.update {
                it.copy(
                    chartState = UiChartState(
                        sparkLine = emptyList(),
                        isLoading = true,
                    )
                )
            }

            viewModelScope.launch {
                when (val priceHistory = getCoinPriceHistoryUseCase.execute(coinId)) {
                    is Result.Success -> {
                        _state.update { currentState ->
                            currentState.copy(
                                chartState = UiChartState(
                                    sparkLine = priceHistory.data.sortedBy { it.timestamp }
                                        .map { it.price },
                                    isLoading = false,
                                    coinName = _state.value.coins.find { it.id == coinId }?.name.orEmpty(),
                                )
                            )
                        }
                    }

                    is Result.Failure -> {
                        _state.update { currentState ->
                            currentState.copy(
                                chartState = UiChartState(
                                    sparkLine = emptyList(),
                                    isLoading = false,
                                    coinName = "",
                                )
                            )
                        }
                    }
                }
            }
        }

        fun onDismissChart() {
            _state.update {
                it.copy(chartState = null)
            }
        }
    }
}