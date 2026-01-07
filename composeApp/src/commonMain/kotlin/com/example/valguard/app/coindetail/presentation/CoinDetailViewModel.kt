package com.example.valguard.app.coindetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.coindetail.domain.ChartTimeframe
import com.example.valguard.app.coindetail.domain.CoinDetailData
import com.example.valguard.app.coindetail.domain.CoinHoldings
import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.coins.domain.usecase.GetCoinDetailsUseCase
import com.example.valguard.app.coins.domain.usecase.GetCoinPriceHistoryUseCase
import com.example.valguard.app.components.ChartPoint
import com.example.valguard.app.core.domain.onSuccess
import com.example.valguard.app.core.util.UiState
import com.example.valguard.app.portfolio.domain.PortfolioRepository
import com.example.valguard.app.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CoinDetailViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val watchlistRepository: WatchlistRepository,
    private val coinGeckoRepository: CoinGeckoRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CoinDetailState())
    val state: StateFlow<CoinDetailState> = _state.asStateFlow()
    
    private var autoRefreshJob: Job? = null
    private companion object {
        const val AUTO_REFRESH_INTERVAL_MS = 60_000L // 1 minute
    }
    
    fun onEvent(event: CoinDetailEvent) {
        when (event) {
            is CoinDetailEvent.LoadCoin -> {
                loadCoin(event.coinId)
                startAutoRefresh(event.coinId)
            }
            is CoinDetailEvent.SelectTimeframe -> selectTimeframe(event.timeframe)
            is CoinDetailEvent.ToggleWatchlist -> toggleWatchlist()
            is CoinDetailEvent.Retry -> retry()
            is CoinDetailEvent.ShowAlertModal -> _state.update { it.copy(showAlertModal = true) }
            is CoinDetailEvent.HideAlertModal -> _state.update { it.copy(showAlertModal = false) }
            is CoinDetailEvent.DismissSnackbar -> _state.update { it.copy(showSnackbar = false, snackbarMessage = null) }
            else -> { /* Navigation events handled by UI */ }
        }
    }
    
    private fun startAutoRefresh(coinId: String) {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(AUTO_REFRESH_INTERVAL_MS)
                loadCoin(coinId, isRefresh = true)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }
    
    private fun loadCoin(coinId: String, isRefresh: Boolean = false) {
        val isNewCoin = coinId != _state.value.coinId
        _state.update { 
            it.copy(
                coinId = coinId,
                chartData = if (isNewCoin) emptyMap() else it.chartData,
                coinData = if (isRefresh && it.coinData is UiState.Success) {
                    UiState.Loading.Refreshing
                } else if (it.coinData is UiState.Success && !isNewCoin) {
                    it.coinData
                } else {
                    UiState.Loading.Initial
                }
            ) 
        }
        
        viewModelScope.launch {
            runCatching {
                getCoinDetailsUseCase.execute(coinId)
            }.onSuccess { result ->
                when (result) {
                    is com.example.valguard.app.core.domain.Result.Success -> {
                        val coinModel = result.data
                        
                        // Fetch detailed data from CoinGecko (description, etc.)
                        getCoinDetailsUseCase.refreshDetail(coinId)
                        val description = getCoinDetailsUseCase.getDescription(coinId)
                        
                        // Get cached coin entity for full market data
                        val coinEntity = coinGeckoRepository.getCoin(coinId)
                        
                        // Build CoinDetailData from REAL API data only
                        val coinDetailData = CoinDetailData(
                            id = coinModel.coin.id,
                            name = coinModel.coin.name,
                            symbol = coinModel.coin.symbol,
                            iconUrl = coinModel.coin.iconUrl,
                            price = coinModel.price,
                            change24h = coinModel.change,
                            // Real data from CoinGecko - null if not available
                            marketCapRank = coinEntity?.marketCapRank,
                            volume24h = coinEntity?.totalVolume,
                            high24h = coinEntity?.high24h,
                            low24h = coinEntity?.low24h,
                            marketCap = coinEntity?.marketCap,
                            circulatingSupply = coinEntity?.circulatingSupply,
                            allTimeHigh = coinEntity?.ath,
                            allTimeLow = coinEntity?.atl,
                            maxSupply = coinEntity?.maxSupply,
                            description = description
                        )
                        
                        _state.update { it.copy(coinData = UiState.Success(coinDetailData), isOffline = false) }
                        
                        fetchPriceHistory(coinModel.coin.id, _state.value.selectedTimeframe)
                        loadHoldings(coinId, coinModel.price)
                    }
                    is com.example.valguard.app.core.domain.Result.Failure -> {
                        _state.update { 
                            it.copy(
                                coinData = UiState.Error("Failed to load coin details"),
                                isOffline = true
                            ) 
                        }
                    }
                }
            }.onFailure { throwable ->
                _state.update { 
                    it.copy(
                        coinData = UiState.Error(throwable.message ?: "An error occurred"),
                        isOffline = true
                    ) 
                }
            }
            
            checkWatchlistStatus(coinId)
        }
    }
    
    private fun fetchPriceHistory(coinId: String, timeframe: ChartTimeframe) {
        viewModelScope.launch {
            _state.update { 
                it.copy(chartData = it.chartData + (timeframe to UiState.Loading.Initial))
            }
            
            runCatching {
                getCoinPriceHistoryUseCase.execute(coinId, timeframe.apiPeriod)
            }.onSuccess { result ->
                when (result) {
                    is com.example.valguard.app.core.domain.Result.Success -> {
                        val history = result.data.sortedBy { it.timestamp }
                        val chartPoints = history.map { priceModel -> 
                            ChartPoint(
                                timestamp = priceModel.timestamp,
                                price = priceModel.price
                            )
                        }
                        
                        _state.update { 
                            it.copy(
                                chartData = it.chartData + (timeframe to if (chartPoints.isEmpty()) {
                                    UiState.Empty()
                                } else {
                                    UiState.Success(chartPoints)
                                })
                            )
                        }
                    }
                    is com.example.valguard.app.core.domain.Result.Failure -> {
                        _state.update { 
                            it.copy(
                                chartData = it.chartData + (timeframe to UiState.Error("Failed to load chart data"))
                            )
                        }
                    }
                }
            }.onFailure { throwable ->
                _state.update { 
                    it.copy(
                        chartData = it.chartData + (timeframe to UiState.Error(throwable.message ?: "Chart error"))
                    )
                }
            }
        }
    }
    
    private suspend fun loadHoldings(coinId: String, currentPrice: Double) {
        portfolioRepository.getPortfolioCoin(coinId)
            .onSuccess { portfolioCoin ->
                if (portfolioCoin != null && portfolioCoin.ownedAmountInUnit > 0) {
                    val currentValue = portfolioCoin.ownedAmountInUnit * currentPrice
                    val costBasis = portfolioCoin.ownedAmountInUnit * portfolioCoin.averagePurchasePrice
                    val profitLoss = currentValue - costBasis
                    val profitLossPercentage = if (costBasis > 0) (profitLoss / costBasis) * 100 else 0.0
                    
                    val holdings = CoinHoldings(
                        coinId = coinId,
                        amountOwned = portfolioCoin.ownedAmountInUnit,
                        averagePurchasePrice = portfolioCoin.averagePurchasePrice,
                        currentValue = currentValue,
                        profitLoss = profitLoss,
                        profitLossPercentage = profitLossPercentage
                    )
                    
                    _state.update { it.copy(holdings = holdings) }
                }
            }
    }
    
    private suspend fun checkWatchlistStatus(coinId: String) {
        try {
            val isInWatchlist = watchlistRepository.isInWatchlist(coinId)
            _state.update { it.copy(isInWatchlist = isInWatchlist) }
        } catch (_: Exception) {
            // Watchlist check failed, default to false
        }
    }
    

    private fun selectTimeframe(timeframe: ChartTimeframe) {
        val currentState = _state.value.chartData[timeframe]
        if (currentState == null || currentState is UiState.Error) {
            _state.update { 
                it.copy(
                    selectedTimeframe = timeframe,
                    chartData = it.chartData + (timeframe to UiState.Loading.Initial)
                )
            }
            fetchPriceHistory(_state.value.coinId, timeframe)
        } else {
            _state.update { it.copy(selectedTimeframe = timeframe) }
        }
    }
    
    private fun toggleWatchlist() {
        val coinId = _state.value.coinId
        val isCurrentlyInWatchlist = _state.value.isInWatchlist
        
        viewModelScope.launch {
            try {
                if (isCurrentlyInWatchlist) {
                    watchlistRepository.removeFromWatchlist(coinId)
                    // Optional: "Removed from Watchlist" (User requested stronger feedback here, reusing snackbar logic for consistency, maybe different text in future)
                    _state.update { 
                        it.copy(
                            isInWatchlist = false,
                            snackbarMessage = "Removed from Watchlist",
                            showSnackbar = true 
                        ) 
                    }
                } else {
                    watchlistRepository.addToWatchlist(coinId)
                    _state.update { 
                        it.copy(
                            isInWatchlist = true,
                            snackbarMessage = "Added to Watchlist",
                            showSnackbar = true
                        ) 
                    }
                }
            } catch (_: Exception) {
                // Failed to toggle watchlist
            }
        }
    }
    
    private fun retry() {
        val coinId = _state.value.coinId
        if (coinId.isNotEmpty()) {
            loadCoin(coinId)
        }
    }
}
