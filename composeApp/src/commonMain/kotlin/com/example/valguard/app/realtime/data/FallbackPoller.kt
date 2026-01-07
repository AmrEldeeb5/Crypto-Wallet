package com.example.valguard.app.realtime.data

import com.example.valguard.app.coins.data.repository.CoinGeckoRepository
import com.example.valguard.app.core.domain.Result
import com.example.valguard.app.realtime.domain.PriceUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class FallbackPoller(
    private val coinGeckoRepository: CoinGeckoRepository,
    private val pollingIntervalMs: Long = 30_000L
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)
    val priceUpdates: Flow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private var pollingJob: Job? = null
    private var subscribedCoinIds = setOf<String>()
    private val previousPrices = mutableMapOf<String, Double>()

    private var _isPolling = false
    val isPolling: Boolean get() = _isPolling

    fun startPolling(coinIds: List<String>) {
        subscribedCoinIds = coinIds.toSet()
        if (subscribedCoinIds.isEmpty()) {
            stopPolling()
            return
        }

        if (pollingJob?.isActive == true) {
            // Already polling, just update the coin IDs
            return
        }

        _isPolling = true
        pollingJob = scope.launch {
            while (isActive) {
                pollPrices()
                delay(pollingIntervalMs)
            }
        }
    }

    fun stopPolling() {
        _isPolling = false
        pollingJob?.cancel()
        pollingJob = null
    }

    fun updateSubscriptions(coinIds: List<String>) {
        subscribedCoinIds = coinIds.toSet()
        if (subscribedCoinIds.isEmpty()) {
            stopPolling()
        }
    }

    private suspend fun pollPrices() {
        if (subscribedCoinIds.isEmpty()) return

        // Refresh coins from CoinGecko API
        when (val refreshResult = coinGeckoRepository.refreshCoins()) {
            is Result.Success -> {
                val timestamp = Clock.System.now().toEpochMilliseconds()
                
                // Get updated coins from cache
                val coins = coinGeckoRepository.getCoinsByIds(subscribedCoinIds.toList())
                
                coins.forEach { coin ->
                    val currentPrice = coin.currentPrice ?: return@forEach
                    val previousPrice = previousPrices[coin.id] ?: currentPrice

                    val priceUpdate = PriceUpdate(
                        coinId = coin.id,
                        price = currentPrice,
                        previousPrice = previousPrice,
                        timestamp = timestamp
                    )

                    previousPrices[coin.id] = currentPrice
                    _priceUpdates.emit(priceUpdate)
                }
            }
            is Result.Failure -> {
                // On failure, try to emit from cached data
                val timestamp = Clock.System.now().toEpochMilliseconds()
                val coins = coinGeckoRepository.getCoinsByIds(subscribedCoinIds.toList())
                
                coins.forEach { coin ->
                    val currentPrice = coin.currentPrice ?: return@forEach
                    val previousPrice = previousPrices[coin.id] ?: currentPrice

                    val priceUpdate = PriceUpdate(
                        coinId = coin.id,
                        price = currentPrice,
                        previousPrice = previousPrice,
                        timestamp = timestamp
                    )

                    previousPrices[coin.id] = currentPrice
                    _priceUpdates.emit(priceUpdate)
                }
            }
        }
    }
}
