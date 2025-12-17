package com.example.cryptowallet.app.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptowallet.app.coins.domain.GetCoinsListUseCase
import com.example.cryptowallet.app.core.domain.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart {
            // onStart is suspend, but we don't want to block collection; also keeps cancellation tied to ViewModel.
            viewModelScope.launch {
                getAllCoins()
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState()
        )

    private suspend fun getAllCoins() {
        when (val coinsResponse = getCoinsListUseCase.execute()) {
            is Result.Success -> {
                _state.update {
                    CoinsState(
                        coins = coinsResponse.data.map { coinItem ->
                            UiCoinListItem(
                                id = coinItem.coin.id,
                                name = coinItem.coin.name,
                                iconUrl = coinItem.coin.iconUrl,
                                symbol = coinItem.coin.symbol,
                                formattedPrice = coinItem.price.toString(), //TODO: formatFiat(coinItem.price),
                                formattedChange = coinItem.change.toString(), //TODO: formatPercentage(coinItem.change),
                                isPositive = coinItem.change >= 0,
                            )
                        }
                    )
                }
            }

            is Result.Failure -> {
                _state.update {
                    it.copy(
                        coins = emptyList(),
                        error = null //TODO: coinsResponse.error.toUiText()
                    )
                }
            }
        }
    }
}