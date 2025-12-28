package com.example.cryptovault.app.realtime.data

import com.example.cryptovault.app.realtime.domain.ConnectionState
import com.example.cryptovault.app.realtime.domain.PriceRepository
import com.example.cryptovault.app.realtime.domain.PriceUpdate
import com.example.cryptovault.app.realtime.domain.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch


class PriceRepositoryImpl(
    private val webSocketClient: WebSocketClient,
    private val fallbackPoller: FallbackPoller
) : PriceRepository {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var _isUsingFallback = false
    override val isUsingFallback: Boolean get() = _isUsingFallback

    private var currentSubscriptions = listOf<String>()

    override val priceUpdates: Flow<PriceUpdate> = merge(
        webSocketClient.priceUpdates,
        fallbackPoller.priceUpdates
    )

    init {
        // Monitor WebSocket connection state and switch to fallback when needed
        scope.launch {
            webSocketClient.connectionState.collect { state ->
                _connectionState.value = state

                when (state) {
                    ConnectionState.CONNECTED -> {
                        // WebSocket connected, stop fallback polling
                        if (_isUsingFallback) {
                            _isUsingFallback = false
                            fallbackPoller.stopPolling()
                        }
                    }
                    ConnectionState.FAILED -> {
                        // WebSocket failed, start fallback polling
                        if (!_isUsingFallback && currentSubscriptions.isNotEmpty()) {
                            _isUsingFallback = true
                            fallbackPoller.startPolling(currentSubscriptions)
                        }
                    }
                    else -> {
                        // Other states - no action needed
                    }
                }
            }
        }
    }

    override suspend fun connect() {
        webSocketClient.connect()
    }

    override suspend fun disconnect() {
        webSocketClient.disconnect()
        fallbackPoller.stopPolling()
        _isUsingFallback = false
    }

    override suspend fun subscribe(coinIds: List<String>) {
        currentSubscriptions = (currentSubscriptions + coinIds).distinct()

        if (_isUsingFallback) {
            fallbackPoller.updateSubscriptions(currentSubscriptions)
        } else {
            webSocketClient.subscribe(coinIds)
        }
    }

    override suspend fun unsubscribe(coinIds: List<String>) {
        currentSubscriptions = currentSubscriptions.filterNot { it in coinIds }

        if (_isUsingFallback) {
            fallbackPoller.updateSubscriptions(currentSubscriptions)
        } else {
            webSocketClient.unsubscribe(coinIds)
        }
    }
}
