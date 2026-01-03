package com.example.valguard.app.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.example.valguard.app.coins.domain.usecase.GetCoinsListUseCase
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.util.formatCrypto
import com.example.valguard.app.core.util.formatFiat
import com.example.valguard.app.core.util.toUiText
import com.example.valguard.app.portfolio.domain.PortfolioCoinModel
import com.example.valguard.app.portfolio.domain.PortfolioRepository
import com.example.valguard.app.realtime.domain.ConnectionState
import com.example.valguard.app.realtime.domain.ObservePriceUpdatesUseCase
import com.example.valguard.app.realtime.domain.PriceDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SCREEN_ID = "coins_list_screen"

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val observePriceUpdatesUseCase: ObservePriceUpdatesUseCase? = null
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

    init {
        // Observe connection state
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.connectionState.collect { connectionState ->
                    _state.update { it.copy(connectionState = connectionState) }
                }
            }

            // Observe price updates
            viewModelScope.launch {
                useCase.allPriceUpdates.collect { priceUpdate ->
                    _state.update { currentState ->
                        currentState.copy(
                            coins = currentState.coins.map { coin ->
                                if (coin.id == priceUpdate.coinId) {
                                    coin.copy(
                                        formattedPrice = formatFiat(priceUpdate.price),
                                        priceDirection = priceUpdate.priceDirection
                                    )
                                } else {
                                    coin
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    private suspend fun getPortfolioHoldings(): Map<String, PortfolioCoinModel> {
        return try {
            when (val result = portfolioRepository.allPortfolioCoinsFlow().firstOrNull()) {
                is Result.Success -> result.data.associateBy { it.coin.id }
                is Result.Failure -> emptyMap()
                null -> emptyMap()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private suspend fun getAllCoins() {
        _state.update { it.copy(isLoading = true, error = null) }

        // Fetch portfolio holdings for merging
        val portfolioHoldings = getPortfolioHoldings()

        when (val coinsResponse = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                val coins = coinsResponse.data.map { coinItem ->
                    val holding = portfolioHoldings[coinItem.coin.id]
                    
                    UiCoinListItem(
                        id = coinItem.coin.id,
                        name = coinItem.coin.name,
                        iconUrl = coinItem.coin.iconUrl,
                        symbol = coinItem.coin.symbol,
                        formattedPrice = formatFiat(coinItem.price),
                        formattedChange = "${if (coinItem.change >= 0) "+" else ""}${String.format("%.2f", coinItem.change)}%",
                        isPositive = coinItem.change >= 0,
                        holdingsAmount = holding?.let { 
                            "${it.ownedAmountInUnit.formatCrypto()} ${coinItem.coin.symbol}"
                        },
                        holdingsValue = holding?.let { 
                            formatFiat(it.ownedAmountInFiat)
                        },
                        sparklineData = coinItem.sparkline,
                        marketCap = if (coinItem.marketCap > 0) formatFiat(coinItem.marketCap, showDecimal = false) else null
                    )
                }

                _state.update {
                    CoinsState(
                        isLoading = false,
                        coins = coins,
                        connectionState = it.connectionState
                    )
                }

                // Subscribe to real-time updates for all coins
                subscribeToRealTimeUpdates(coins.map { it.id })
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
    }

    private fun subscribeToRealTimeUpdates(coinIds: List<String>) {
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.start()
                useCase.subscribeScreen(SCREEN_ID, coinIds)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Unsubscribe when ViewModel is cleared
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.unsubscribeScreen(SCREEN_ID)
            }
        }
    }

    fun onCoinLongPressed(coinId: String) {
        val coin = _state.value.coins.find { it.id == coinId }
        val coinName = coin?.name.orEmpty()
        val coinSymbol = coin?.symbol.orEmpty()
        
        // Extract change percent from formatted change (e.g., "$2" -> 2.0)
        val changePercent = coin?.formattedChange
            ?.replace("$", "")
            ?.replace(",", "")
            ?.toDoubleOrNull() ?: 0.0
        
        _state.update {
            it.copy(
                chartState = UiChartState(
                    sparkLine = emptyList(),
                    isLoading = true,
                    coinName = coinName,
                    coinSymbol = coinSymbol,
                    changePercent = changePercent
                )
            )
        }

        viewModelScope.launch {
            when (val priceHistory = getCoinPriceHistoryUseCase.execute(coinId, "24h")) {
                is Result.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = UiChartState(
                                sparkLine = priceHistory.data.sortedBy { it.timestamp }
                                    .map { it.price },
                                isLoading = false,
                                coinName = coinName,
                                coinSymbol = coinSymbol,
                                changePercent = changePercent
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
                                coinName = coinName,
                                coinSymbol = coinSymbol,
                                changePercent = changePercent,
                                error = "Could not load chart data"
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

    fun onSearchQueryChange(query: String) {
        _state.update {
            it.copy(searchQuery = query)
        }
    }

    fun onClearSearch() {
        _state.update {
            it.copy(searchQuery = "")
        }
    }
}