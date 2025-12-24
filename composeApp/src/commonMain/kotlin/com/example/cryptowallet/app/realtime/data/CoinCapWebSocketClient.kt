package com.example.cryptowallet.app.realtime.data

import com.example.cryptowallet.app.realtime.domain.ConnectionState
import com.example.cryptowallet.app.realtime.domain.PriceUpdate
import com.example.cryptowallet.app.realtime.domain.ReconnectionStrategy
import com.example.cryptowallet.app.realtime.domain.WebSocketClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * CoinCap WebSocket client for real-time cryptocurrency price updates.
 * 
 * CoinCap WebSocket API:
 * - URL: wss://ws.coincap.io/prices?assets=bitcoin,ethereum,dogecoin
 * - Message format: {"bitcoin":"43521.23","ethereum":"2341.56"}
 * - No subscription messages needed - assets are specified in URL
 * - Reconnection requires new connection with updated asset list
 */
class CoinCapWebSocketClient(
    private val httpClient: HttpClient,
    private val reconnectionStrategy: ReconnectionStrategy
) : WebSocketClient {

    companion object {
        private const val BASE_URL = "wss://ws.coincap.io/prices"
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val json = Json { ignoreUnknownKeys = true }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _priceUpdates = MutableSharedFlow<PriceUpdate>(extraBufferCapacity = 64)
    override val priceUpdates: Flow<PriceUpdate> = _priceUpdates.asSharedFlow()

    private var session: WebSocketSession? = null
    private var messageJob: Job? = null
    private var reconnectAttempt = 0

    // Track subscribed coins and previous prices
    private val subscribedCoins = mutableSetOf<String>()
    private val previousPrices = mutableMapOf<String, Double>()

    override suspend fun connect() {
        if (_connectionState.value == ConnectionState.CONNECTED ||
            _connectionState.value == ConnectionState.CONNECTING
        ) {
            return
        }

        if (subscribedCoins.isEmpty()) {
            // No coins to subscribe to yet, stay disconnected
            return
        }

        _connectionState.value = ConnectionState.CONNECTING

        try {
            val url = buildWebSocketUrl()
            session = httpClient.webSocketSession(url)
            _connectionState.value = ConnectionState.CONNECTED
            reconnectAttempt = 0
            reconnectionStrategy.reset()
            startMessageListener()
        } catch (e: Exception) {
            handleConnectionFailure()
        }
    }

    override suspend fun disconnect() {
        messageJob?.cancel()
        messageJob = null

        try {
            session?.close()
        } catch (e: Exception) {
            // Ignore close errors
        }

        session = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }


    override suspend fun subscribe(coinIds: List<String>) {
        if (coinIds.isEmpty()) return

        val newCoins = coinIds.filter { it !in subscribedCoins }
        if (newCoins.isEmpty()) return

        subscribedCoins.addAll(newCoins)

        // CoinCap requires reconnection to change subscriptions
        // Disconnect and reconnect with updated asset list
        if (_connectionState.value == ConnectionState.CONNECTED) {
            reconnectWithNewSubscriptions()
        } else {
            connect()
        }
    }

    override suspend fun unsubscribe(coinIds: List<String>) {
        if (coinIds.isEmpty()) return

        val removedCoins = coinIds.filter { it in subscribedCoins }
        if (removedCoins.isEmpty()) return

        subscribedCoins.removeAll(removedCoins.toSet())
        previousPrices.keys.removeAll(removedCoins.toSet())

        if (subscribedCoins.isEmpty()) {
            disconnect()
        } else if (_connectionState.value == ConnectionState.CONNECTED) {
            reconnectWithNewSubscriptions()
        }
    }

    private suspend fun reconnectWithNewSubscriptions() {
        // Close current session
        messageJob?.cancel()
        try {
            session?.close()
        } catch (e: Exception) {
            // Ignore
        }
        session = null

        // Reconnect with new URL
        _connectionState.value = ConnectionState.CONNECTING
        try {
            val url = buildWebSocketUrl()
            session = httpClient.webSocketSession(url)
            _connectionState.value = ConnectionState.CONNECTED
            startMessageListener()
        } catch (e: Exception) {
            handleConnectionFailure()
        }
    }

    private fun buildWebSocketUrl(): String {
        // CoinCap uses lowercase coin IDs (e.g., "bitcoin", "ethereum")
        val assets = subscribedCoins.joinToString(",") { it.lowercase() }
        return "$BASE_URL?assets=$assets"
    }

    private fun startMessageListener() {
        messageJob?.cancel()
        messageJob = scope.launch {
            val currentSession = session ?: return@launch

            try {
                for (frame in currentSession.incoming) {
                    if (!isActive) break

                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            parseCoinCapMessage(text)
                        }
                        else -> { /* Ignore other frame types */ }
                    }
                }
            } catch (e: Exception) {
                // Connection lost
                handleConnectionFailure()
            }
        }
    }

    /**
     * Parses CoinCap message format: {"bitcoin":"43521.23","ethereum":"2341.56"}
     */
    private suspend fun parseCoinCapMessage(text: String) {
        try {
            val jsonObject = json.decodeFromString<JsonObject>(text)
            val timestamp = Clock.System.now().toEpochMilliseconds()

            for ((coinId, priceElement) in jsonObject) {
                val priceString = priceElement.jsonPrimitive.content
                val currentPrice = priceString.toDoubleOrNull() ?: continue
                val previousPrice = previousPrices[coinId] ?: currentPrice

                val priceUpdate = PriceUpdate(
                    coinId = coinId,
                    price = currentPrice,
                    previousPrice = previousPrice,
                    timestamp = timestamp
                )

                // Update previous price for next comparison
                previousPrices[coinId] = currentPrice

                _priceUpdates.emit(priceUpdate)
            }
        } catch (e: Exception) {
            // Log parse error, skip malformed message
        }
    }

    private suspend fun handleConnectionFailure() {
        session = null
        messageJob?.cancel()
        messageJob = null

        if (reconnectionStrategy.shouldFallback(reconnectAttempt)) {
            _connectionState.value = ConnectionState.FAILED
            return
        }

        _connectionState.value = ConnectionState.RECONNECTING
        val delayMs = reconnectionStrategy.nextDelay(reconnectAttempt)
        reconnectAttempt++

        delay(delayMs)
        connect()
    }
}
