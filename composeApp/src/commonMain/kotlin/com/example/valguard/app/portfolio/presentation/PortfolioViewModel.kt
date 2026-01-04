package com.example.valguard.app.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valguard.app.core.domain.DataError
import com.example.valguard.app.portfolio.domain.PortfolioRepository
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.core.util.formatCoinUnit
import com.example.valguard.app.core.util.formatFiat
import com.example.valguard.app.core.util.formatPercentage
import com.example.valguard.app.core.util.toUiText
import com.example.valguard.app.portfolio.domain.PortfolioCoinModel
import com.example.valguard.app.realtime.domain.ObservePriceUpdatesUseCase
import com.example.valguard.app.realtime.domain.PriceDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SCREEN_ID = "portfolio_screen"

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
    private val observePriceUpdatesUseCase: ObservePriceUpdatesUseCase? = null
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState(isLoading = true))
    private val _priceDirections = MutableStateFlow<Map<String, PriceDirection>>(emptyMap())

    val state: StateFlow<PortfolioState> = combine(
        _state,
        portfolioRepository.allPortfolioCoinsFlow(),
        portfolioRepository.totalBalanceFlow(),
        portfolioRepository.cashBalanceFlow(),
        _priceDirections
    ) { currentState, portfolioCoinsResponse, totalBalanceResult, cashBalance, priceDirections ->
        when (portfolioCoinsResponse) {
            is Result.Success -> {
                handleSuccessState(
                    currentState = currentState,
                    portfolioCoins = portfolioCoinsResponse.data,
                    totalBalanceResult = totalBalanceResult,
                    cashBalance = cashBalance,
                    priceDirections = priceDirections
                )
            }
            is Result.Failure -> {
                handleErrorState(
                    currentState = currentState,
                    portfolioCoinsResponse.error
                )
            }
        }
    }.onStart {
        portfolioRepository.initializeBalance()
        setupRealTimeUpdates()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = PortfolioState(isLoading = true)
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
                    _priceDirections.update { directions ->
                        directions + (priceUpdate.coinId to priceUpdate.priceDirection)
                    }
                }
            }
        }
    }

    private fun setupRealTimeUpdates() {
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                // Subscribe to owned coins when portfolio loads
                portfolioRepository.allPortfolioCoinsFlow().collect { result ->
                    if (result is Result.Success) {
                        val coinIds = result.data.map { it.coin.id }
                        if (coinIds.isNotEmpty()) {
                            useCase.start()
                            useCase.subscribeScreen(SCREEN_ID, coinIds)
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.unsubscribeScreen(SCREEN_ID)
            }
        }
    }

    private fun handleSuccessState(
        currentState: PortfolioState,
        portfolioCoins: List<PortfolioCoinModel>,
        totalBalanceResult: Result<Double, DataError>,
        cashBalance: Double,
        priceDirections: Map<String, PriceDirection>
    ): PortfolioState {
        val portfolioValue = when (totalBalanceResult) {
            is Result.Success -> formatFiat(totalBalanceResult.data)
            is Result.Failure -> formatFiat(0.0)
        }
        
        // Calculate overall portfolio performance from individual coin performances
        val totalPerformance = if (portfolioCoins.isNotEmpty()) {
            val weightedPerformance = portfolioCoins.sumOf { coin ->
                coin.performancePercent * coin.ownedAmountInFiat
            }
            val totalValue = portfolioCoins.sumOf { it.ownedAmountInFiat }
            if (totalValue > 0) weightedPerformance / totalValue else 0.0
        } else {
            0.0
        }

        return currentState.copy(
            coins = portfolioCoins.map { it.toUiPortfolioCoinItem(priceDirections) },
            portfolioValue = portfolioValue,
            cashBalance = formatFiat(cashBalance),
            performancePercent = formatPercentage(totalPerformance),
            isPerformancePositive = totalPerformance >= 0,
            showBuyButton = portfolioCoins.isNotEmpty(),
            isLoading = false,
        )
    }

    private fun handleErrorState(
        currentState: PortfolioState,
        error: DataError,
    ): PortfolioState {
        return currentState.copy(
            isLoading = false,
            error = error.toUiText()
        )
    }

    private fun PortfolioCoinModel.toUiPortfolioCoinItem(
        priceDirections: Map<String, PriceDirection>
    ): UiPortfolioCoinItem {
        return UiPortfolioCoinItem(
            id = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            amountInUnitText = formatCoinUnit(ownedAmountInUnit, coin.symbol),
            amountInFiatText = formatFiat(ownedAmountInFiat),
            performancePercentText = formatPercentage(performancePercent),
            performancePercent = performancePercent,
            isPositive = performancePercent >= 0,
            priceDirection = priceDirections[coin.id] ?: PriceDirection.UNCHANGED
        )
    }
}