package com.example.cryptovault.app.trade.presentation.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptovault.app.coins.domain.usecase.GetCoinDetailsUseCase
import com.example.cryptovault.app.core.domain.Result
import com.example.cryptovault.app.core.util.formatFiat
import com.example.cryptovault.app.core.util.toUiText
import com.example.cryptovault.app.portfolio.domain.PortfolioRepository
import com.example.cryptovault.app.realtime.domain.ObservePriceUpdatesUseCase
import com.example.cryptovault.app.trade.domain.BuyCoinUseCase
import com.example.cryptovault.app.trade.presentation.common.TradeEvent
import com.example.cryptovault.app.trade.presentation.common.TradeState
import com.example.cryptovault.app.trade.presentation.common.UiTradeCoinItem
import com.example.cryptovault.app.trade.presentation.common.ValidationResult
import com.example.cryptovault.app.trade.presentation.mapper.toCoin
import cryptovault.composeapp.generated.resources.Res
import cryptovault.composeapp.generated.resources.error_insufficient_balance
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SCREEN_ID = "buy_screen"

class BuyViewModel(
    private val coinId: String,
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val portfolioRepository: PortfolioRepository,
    private val buyCoinUseCase: BuyCoinUseCase,
    private val observePriceUpdatesUseCase: ObservePriceUpdatesUseCase? = null
) : ViewModel() {

    private val _events = MutableSharedFlow<TradeEvent>()
    val events: SharedFlow<TradeEvent> = _events.asSharedFlow()

    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())
    val state = combine(
        _state,
        _amount,
    ) { state, amount ->
        val validation = validateAmount(amount, state.availableAmountValue)
        state.copy(
            amount = amount,
            isAmountValid = validation is ValidationResult.Valid,
            validationError = when (validation) {
                is ValidationResult.InsufficientFunds -> Res.string.error_insufficient_balance
                else -> null
            }
        )
    }.onStart {
        val balance = portfolioRepository.cashBalanceFlow().first()
        getCoinDetails(balance)
        setupRealTimeUpdates()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true)
    )

    init {
        // Observe connection state
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.connectionState.collect { connectionState ->
                    _state.update { it.copy(connectionState = connectionState) }
                }
            }

            // Observe price updates for the specific coin
            viewModelScope.launch {
                useCase.priceUpdatesFor(coinId).collect { priceUpdate ->
                    _state.update { currentState ->
                        currentState.coin?.let { coin ->
                            currentState.copy(
                                coin = coin.copy(
                                    price = priceUpdate.price,
                                    priceDirection = priceUpdate.priceDirection
                                )
                            )
                        } ?: currentState
                    }
                }
            }
        }
    }

    private fun setupRealTimeUpdates() {
        observePriceUpdatesUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.start()
                useCase.subscribeScreen(SCREEN_ID, listOf(coinId))
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

    private suspend fun getCoinDetails(balance: Double) {
        when (val coinResponse = getCoinDetailsUseCase.execute(coinId)) {
            is Result.Success -> {
                _state.update {
                    it.copy(
                        coin = UiTradeCoinItem(
                            id = coinResponse.data.coin.id,
                            name = coinResponse.data.coin.name,
                            symbol = coinResponse.data.coin.symbol,
                            iconUrl = coinResponse.data.coin.iconUrl,
                            price = coinResponse.data.price,
                        ),
                        availableAmount = "Available: ${formatFiat(balance)}",
                        availableAmountValue = balance
                    )
                }
            }

            is Result.Failure -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = coinResponse.error.toUiText()
                    )
                }
            }
        }
    }

    private fun validateAmount(amount: String, availableBalance: Double): ValidationResult {
        if (amount.isBlank()) return ValidationResult.Empty
        val amountValue = amount.toDoubleOrNull() ?: return ValidationResult.Empty
        if (amountValue <= 0) return ValidationResult.Zero
        if (amountValue > availableBalance) return ValidationResult.InsufficientFunds(availableBalance)
        return ValidationResult.Valid
    }

    fun onAmountChanged(amount: String) {
        _amount.value = amount
    }

    fun onSubmitClicked() {
        if (!state.value.isAmountValid) return
        _state.update { it.copy(showConfirmation = true) }
    }

    fun onConfirmTrade() {
        val tradeCoin = state.value.coin ?: return
        val amountValue = _amount.value.toDoubleOrNull() ?: return

        _state.update { it.copy(isExecuting = true) }

        viewModelScope.launch {
            val buyCoinResponse = buyCoinUseCase.buyCoin(
                coin = tradeCoin.toCoin(),
                amountInFiat = amountValue,
                price = tradeCoin.price,
            )

            when (buyCoinResponse) {
                is Result.Success -> {
                    _state.update { it.copy(showConfirmation = false, isExecuting = false) }
                    _events.emit(TradeEvent.NavigateToPortfolio)
                }
                is Result.Failure -> {
                    _state.update {
                        it.copy(
                            isExecuting = false,
                            showConfirmation = false,
                            error = buyCoinResponse.error.toUiText(),
                        )
                    }
                }
            }
        }
    }

    fun onCancelConfirmation() {
        _state.update { it.copy(showConfirmation = false) }
    }

    @Deprecated("Use onSubmitClicked() instead", ReplaceWith("onSubmitClicked()"))
    fun onBuyClicked() {
        onSubmitClicked()
    }
}