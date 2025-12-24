package com.example.cryptowallet.app.realtime.data

import com.example.cryptowallet.app.coins.domain.api.CoinsRemoteDataSource
import com.example.cryptowallet.app.core.domain.onSuccess
import com.example.cryptowallet.app.realtime.domain.PriceUpdate
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

/**
 * Fallback mechanism that polls the REST API for price updates
 * when WebSocket connection fails.
 *
 * @param coinsRemoteDataSource Data source for fetching coin prices
 * @param pollingIntervalMs Interval between polls in milliseconds (default: 30 seconds)
 */
class FallbackPoller(
    private val coinsRemoteDataSource: CoinsRemoteDataSource,
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

    /**
     * Start polling for price updates.
     *
     * @param coinIds List of coin IDs to poll for
     */
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

    /**
     * Stop polling for price updates.
     */
    fun stopPolling() {
        _isPolling = false
        pollingJob?.cancel()
        pollingJob = null
    }

    /**
     * Update the list of coin IDs to poll for.
     */
    fun updateSubscriptions(coinIds: List<String>) {
        subscribedCoinIds = coinIds.toSet()
        if (subscribedCoinIds.isEmpty()) {
            stopPolling()
        }
    }

    private suspend fun pollPrices() {
        if (subscribedCoinIds.isEmpty()) return

        coinsRemoteDataSource.getListOfCoins()
            .onSuccess { response ->
                val timestamp = Clock.System.now().toEpochMilliseconds()

                response.data.coins
                    .filter { it.uuid in subscribedCoinIds }
                    .forEach { coin ->
                        val currentPrice = coin.price?.toDoubleOrNull() ?: return@forEach
                        val previousPrice = previousPrices[coin.uuid] ?: currentPrice

                        val priceUpdate = PriceUpdate(
                            coinId = coin.uuid,
                            price = currentPrice,
                            previousPrice = previousPrice,
                            timestamp = timestamp
                        )

                        previousPrices[coin.uuid] = currentPrice
                        _priceUpdates.emit(priceUpdate)
                    }
            }
    }
}
