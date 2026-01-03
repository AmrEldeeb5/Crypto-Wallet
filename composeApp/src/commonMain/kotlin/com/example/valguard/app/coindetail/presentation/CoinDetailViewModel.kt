package com.example.valguard.app.coindetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.coindetail.domain.ChartTimeframe
import com.example.valguard.app.coindetail.domain.CoinDetailData
import com.example.valguard.app.coindetail.domain.CoinHoldings
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
    private val watchlistRepository: WatchlistRepository
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
        // Set Loading state - but keep existing data if refreshing
        _state.update { 
            it.copy(
                coinId = coinId, 
                coinData = if (isRefresh && it.coinData is UiState.Success) it.coinData else UiState.Loading
            ) 
        }
        
        viewModelScope.launch {
            // Use runCatching to guarantee all code paths exit Loading state
            runCatching {
                getCoinDetailsUseCase.execute(coinId)
            }.onSuccess { result ->
                when (result) {
                    is com.example.valguard.app.core.domain.Result.Success -> {
                        val coinModel = result.data
                        // Generate mock rank based on coin id hash (consistent per coin)
                        val mockRank = (coinModel.coin.id.hashCode().and(0x7FFFFFFF) % 100) + 1
                        
                        val coinDetailData = CoinDetailData(
                            id = coinModel.coin.id,
                            name = coinModel.coin.name,
                            symbol = coinModel.coin.symbol,
                            iconUrl = coinModel.coin.iconUrl,
                            price = coinModel.price,
                            change24h = coinModel.change,
                            marketCapRank = mockRank,
                            volume24h = coinModel.price * 1_000_000,
                            high24h = coinModel.price * 1.05,
                            low24h = coinModel.price * 0.95,
                            marketCap = coinModel.price * 19_500_000,
                            circulatingSupply = 19_500_000.0,
                            allTimeHigh = coinModel.price * 1.5,
                            allTimeLow = coinModel.price * 0.1,
                            maxSupply = 21_000_000.0,
                            description = "A decentralized digital currency that enables instant payments to anyone, anywhere in the world."
                        )
                        
                        // Update state to Success
                        _state.update { it.copy(coinData = UiState.Success(coinDetailData), isOffline = false) }
                        
                        // Fetch chart data for current timeframe
                        fetchPriceHistory(coinModel.coin.id, _state.value.selectedTimeframe)
                        
                        // Load holdings after coin data is loaded
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
                // Handles exceptions, cancellation, etc. - always exits Loading
                _state.update { 
                    it.copy(
                        coinData = UiState.Error(throwable.message ?: "An error occurred"),
                        isOffline = true
                    ) 
                }
            }
            
            // Check watchlist status
            checkWatchlistStatus(coinId)
        }
    }
    
    private fun fetchPriceHistory(coinId: String, timeframe: ChartTimeframe) {
        viewModelScope.launch {
            _state.update { 
                it.copy(chartData = it.chartData + (timeframe to UiState.Loading))
            }
            
            runCatching {
                getCoinPriceHistoryUseCase.execute(coinId, timeframe.apiPeriod)
            }.onSuccess { result ->
                when (result) {
                    is com.example.valguard.app.core.domain.Result.Success -> {
                        val history = result.data.sortedBy { it.timestamp }
                        // Map PriceModel to ChartPoint with timestamp and price
                        val chartPoints = history.map { priceModel -> 
                            ChartPoint(
                                timestamp = priceModel.timestamp,
                                price = priceModel.price
                            )
                        }
                        
                        _state.update { 
                            it.copy(
                                chartData = it.chartData + (timeframe to if (chartPoints.isEmpty()) {
                                    UiState.Empty
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
        _state.update { it.copy(selectedTimeframe = timeframe) }
        val currentState = _state.value.chartData[timeframe]
        // Allow re-fetch if no data exists OR if previous attempt failed
        if (currentState == null || currentState is UiState.Error) {
            fetchPriceHistory(_state.value.coinId, timeframe)
        }
    }
    
    private fun toggleWatchlist() {
        val coinId = _state.value.coinId
        val isCurrentlyInWatchlist = _state.value.isInWatchlist
        
        viewModelScope.launch {
            try {
                if (isCurrentlyInWatchlist) {
                    watchlistRepository.removeFromWatchlist(coinId)
                } else {
                    watchlistRepository.addToWatchlist(coinId)
                }
                _state.update { it.copy(isInWatchlist = !isCurrentlyInWatchlist) }
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
