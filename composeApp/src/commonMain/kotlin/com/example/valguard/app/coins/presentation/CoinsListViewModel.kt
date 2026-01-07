package com.example.valguard.app.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.example.valguard.app.coins.domain.usecase.GetCoinsListUseCase
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.util.formatCompactNumber
import com.example.valguard.app.core.util.formatCrypto
import com.example.valguard.app.core.util.formatFiat
import com.example.valguard.app.core.util.toUiText
import com.example.valguard.app.portfolio.domain.PortfolioCoinModel
import com.example.valguard.app.portfolio.domain.PortfolioRepository
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
            observeCoins()
            refreshIfNeeded()
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

    /**
     * Observe coins from database (cache-first)
     */
    private fun observeCoins() {
        viewModelScope.launch {
            getCoinsListUseCase.observe().collect { coinModels ->
                if (coinModels.isEmpty()) {
                    // No cached data yet, show loading
                    _state.update { it.copy(isLoading = true) }
                    return@collect
                }

                val portfolioHoldings = getPortfolioHoldings()

                val coins = coinModels.map { coinItem ->
                    val holding = portfolioHoldings[coinItem.coin.id]

                    // Format change percentage
                    val changeValue = (coinItem.change * 100).toInt() / 100.0
                    val formattedChange = buildString {
                        if (coinItem.change >= 0) append("+")
                        append(changeValue.toString())
                        append("%")
                    }

                    UiCoinListItem(
                        id = coinItem.coin.id,
                        name = coinItem.coin.name,
                        iconUrl = coinItem.coin.iconUrl,
                        symbol = coinItem.coin.symbol,
                        formattedPrice = formatFiat(coinItem.price),
                        formattedChange = formattedChange,
                        changePercent = coinItem.change,
                        isPositive = coinItem.change >= 0,
                        holdingsAmount = holding?.let {
                            "${it.ownedAmountInUnit.formatCrypto()} ${coinItem.coin.symbol}"
                        },
                        holdingsValue = holding?.let {
                            formatFiat(it.ownedAmountInFiat)
                        },
                        sparklineData = coinItem.sparkline,
                        marketCap = if (coinItem.marketCap > 0) formatCompactNumber(coinItem.marketCap) else null
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        coins = coins,
                        error = null
                    )
                }

                // Subscribe to real-time updates for all coins
                subscribeToRealTimeUpdates(coins.map { it.id })
            }
        }
    }

    /**
     * Refresh from API if cache is stale
     */
    private fun refreshIfNeeded() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = _state.value.coins.isEmpty()) }
            
            when (val result = getCoinsListUseCase.refreshIfNeeded()) {
                is Result.Success -> {
                    // Data will flow through observeCoins()
                }
                is Result.Failure -> {
                    // Only show error if we have no cached data
                    if (_state.value.coins.isEmpty()) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.error.toUiText()
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Force refresh (pull-to-refresh)
     */
    fun onRefresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            
            when (val result = getCoinsListUseCase.forceRefresh()) {
                is Result.Success -> {
                    // Data will flow through observeCoins()
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(error = result.error.toUiText())
                    }
                }
            }
            
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun getPortfolioHoldings(): Map<String, PortfolioCoinModel> {
        return try {
            when (val result = portfolioRepository.allPortfolioCoinsFlow().firstOrNull()) {
                is Result.Success -> result.data.associateBy { it.coin.id }
                is Result.Failure -> emptyMap()
                null -> emptyMap()
            }
        } catch (_: Exception) {
            emptyMap()
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
        
        // Extract change percent from formatted change (e.g., "+2.5%" -> 2.5)
        val changePercent = coin?.changePercent ?: 0.0
        
        // Set loading state immediately
        _state.update {
            it.copy(
                chartState = ChartState.Loading(
                    coinName = coinName,
                    coinSymbol = coinSymbol,
                    changePercent = changePercent
                )
            )
        }

        viewModelScope.launch {
            when (val priceHistory = getCoinPriceHistoryUseCase.execute(coinId, "24h")) {
                is Result.Success -> {
                    val sparkLine = priceHistory.data
                        .sortedBy { it.timestamp }
                        .map { it.price }
                    
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = if (sparkLine.isEmpty()) {
                                // CoinGecko returned empty data (stablecoin, rate limit, etc.)
                                ChartState.Empty(
                                    coinName = coinName,
                                    coinSymbol = coinSymbol,
                                    changePercent = changePercent
                                )
                            } else {
                                ChartState.Success(
                                    sparkLine = sparkLine,
                                    coinName = coinName,
                                    coinSymbol = coinSymbol,
                                    changePercent = changePercent
                                )
                            }
                        )
                    }
                }

                is Result.Failure -> {
                    _state.update { currentState ->
                        currentState.copy(
                            chartState = ChartState.Error(
                                message = "Could not load chart data",
                                coinName = coinName,
                                coinSymbol = coinSymbol,
                                changePercent = changePercent
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