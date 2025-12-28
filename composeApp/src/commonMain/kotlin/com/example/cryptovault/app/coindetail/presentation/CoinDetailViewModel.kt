package com.example.cryptovault.app.coindetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptovault.app.coindetail.domain.ChartTimeframe
import com.example.cryptovault.app.coindetail.domain.CoinDetailData
import com.example.cryptovault.app.coindetail.domain.CoinHoldings
import com.example.cryptovault.app.coins.domain.usecase.GetCoinDetailsUseCase
import com.example.cryptovault.app.core.domain.onError
import com.example.cryptovault.app.core.domain.onSuccess
import com.example.cryptovault.app.core.util.UiState
import com.example.cryptovault.app.portfolio.domain.PortfolioRepository
import com.example.cryptovault.app.watchlist.domain.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.math.sin
import kotlin.random.Random

class CoinDetailViewModel(
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(CoinDetailState())
    val state: StateFlow<CoinDetailState> = _state.asStateFlow()
    
    // Random seed for consistent chart data per coin
    private var chartSeed: Long = Clock.System.now().toEpochMilliseconds()
    
    fun onEvent(event: CoinDetailEvent) {
        when (event) {
            is CoinDetailEvent.LoadCoin -> loadCoin(event.coinId)
            is CoinDetailEvent.SelectTimeframe -> selectTimeframe(event.timeframe)
            is CoinDetailEvent.ToggleWatchlist -> toggleWatchlist()
            is CoinDetailEvent.Retry -> retry()
            is CoinDetailEvent.Refresh -> refreshData()
            is CoinDetailEvent.ShowAlertModal -> _state.update { it.copy(showAlertModal = true) }
            is CoinDetailEvent.HideAlertModal -> _state.update { it.copy(showAlertModal = false) }
            else -> { /* Navigation events handled by UI */ }
        }
    }
    
    private fun loadCoin(coinId: String) {
        _state.update { it.copy(coinId = coinId, coinData = UiState.Loading, isRefreshing = false) }
        chartSeed = coinId.hashCode().toLong()
        
        viewModelScope.launch {
            getCoinDetailsUseCase.execute(coinId)
                .onSuccess { coinModel ->
                    // Generate mock rank based on coin id hash (consistent per coin)
                    val mockRank = (coinModel.coin.id.hashCode().and(0x7FFFFFFF) % 100) + 1
                    
                    val coinDetailData = CoinDetailData(
                        id = coinModel.coin.id,
                        name = coinModel.coin.name,
                        symbol = coinModel.coin.symbol,
                        iconUrl = coinModel.coin.iconUrl,
                        price = coinModel.price,
                        change24h = coinModel.change,
                        marketCapRank = mockRank, // Mock rank - would come from detailed API
                        volume24h = coinModel.price * 1_000_000 * Random(chartSeed).nextDouble(0.5, 2.0),
                        high24h = coinModel.price * 1.05,
                        low24h = coinModel.price * 0.95,
                        marketCap = coinModel.price * 19_500_000, // Mock circulating supply
                        circulatingSupply = 19_500_000.0, // Mock for BTC-like
                        allTimeHigh = coinModel.price * 1.5 // Mock ATH
                    )
                    
                    // Generate chart data for all timeframes
                    val chartData = generateAllChartData(coinModel.price)
                    
                    _state.update { 
                        it.copy(
                            coinData = UiState.Success(coinDetailData),
                            chartData = chartData,
                            isRefreshing = false
                        ) 
                    }
                    
                    // Load holdings after coin data is loaded
                    loadHoldings(coinId, coinModel.price)
                }
                .onError { error ->
                    _state.update { 
                        it.copy(
                            coinData = UiState.Error("Failed to load coin details"),
                            isOffline = true,
                            isRefreshing = false
                        ) 
                    }
                }
            
            // Check watchlist status
            checkWatchlistStatus(coinId)
        }
    }
    
    private fun generateAllChartData(currentPrice: Double): Map<ChartTimeframe, List<Float>> {
        return ChartTimeframe.entries.associateWith { timeframe ->
            generateMockChartData(timeframe, currentPrice)
        }
    }
    
    fun generateMockChartData(timeframe: ChartTimeframe, currentPrice: Double): List<Float> {
        val random = Random(chartSeed + timeframe.ordinal)
        val dataPoints = when (timeframe) {
            ChartTimeframe.DAY_1 -> 24    // Hourly for 24h
            ChartTimeframe.WEEK_1 -> 7    // Daily for 7d
            ChartTimeframe.MONTH_1 -> 30  // Daily for 1M
            ChartTimeframe.MONTH_3 -> 12  // Weekly for 3M
            ChartTimeframe.YEAR_1 -> 12   // Monthly for 1Y
            ChartTimeframe.ALL -> 12      // Use 1Y data for ALL
        }
        
        val prices = mutableListOf<Float>()
        var price = currentPrice * (1 - random.nextDouble(0.02, 0.05)) // Start slightly lower
        
        // Generate price points with realistic fluctuations
        for (i in 0 until dataPoints) {
            // Add some trend + noise
            val trend = sin(i.toDouble() / dataPoints * 3.14159) * 0.02 // Slight wave pattern
            val noise = random.nextDouble(-0.015, 0.015) // +/- 1.5% noise
            val change = trend + noise
            
            price *= (1 + change)
            prices.add(price.toFloat())
        }
        
        // Ensure the last price is close to current price
        if (prices.isNotEmpty()) {
            val adjustment = currentPrice / prices.last()
            for (i in prices.indices) {
                // Gradually adjust towards current price
                val factor = i.toFloat() / prices.size
                prices[i] = (prices[i] * (1 + (adjustment - 1) * factor)).toFloat()
            }
        }
        
        return prices
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
        } catch (e: Exception) {
            // Watchlist check failed, default to false
        }
    }
    
    private fun selectTimeframe(timeframe: ChartTimeframe) {
        _state.update { it.copy(selectedTimeframe = timeframe) }
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
            } catch (e: Exception) {
                // Failed to toggle watchlist
            }
        }
    }
    
    fun refreshData() {
        val coinId = _state.value.coinId
        if (coinId.isNotEmpty()) {
            _state.update { it.copy(isRefreshing = true) }
            loadCoin(coinId)
        }
    }
    
    private fun retry() {
        val coinId = _state.value.coinId
        if (coinId.isNotEmpty()) {
            loadCoin(coinId)
        }
    }
}
