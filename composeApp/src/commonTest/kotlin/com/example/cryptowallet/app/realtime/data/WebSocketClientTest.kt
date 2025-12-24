package com.example.cryptowallet.app.realtime.data

import com.example.cryptowallet.app.realtime.TestGenerators
import com.example.cryptowallet.app.realtime.domain.ConnectionState
import com.example.cryptowallet.app.realtime.domain.PriceUpdate
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

/**
 * Property-based tests for WebSocketClient behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WebSocketClientTest {

    /**
     * **Feature: realtime-price-updates, Property 7: Price updates flow through observable stream**
     *
     * For any price update received by the WebSocketClient, that update SHALL be emitted
     * through the priceUpdates Flow.
     *
     * Validates: Requirements 4.3
     */
    @Test
    fun `Property 7 - price updates flow through observable stream`() = runTest(UnconfinedTestDispatcher()) {
        val priceUpdateArb = Arb.list(
            io.kotest.property.arbitrary.arbitrary {
                PriceUpdate(
                    coinId = TestGenerators.simpleCoinIdArb.bind(),
                    price = TestGenerators.priceArb.bind(),
                    previousPrice = TestGenerators.priceArb.bind(),
                    timestamp = TestGenerators.timestampArb.bind()
                )
            },
            1..10
        )

        checkAll(100, priceUpdateArb) { priceUpdates ->
            val client = FakeWebSocketClient()
            client.connect()

            // Start collecting in async
            val receivedDeferred = async {
                client.priceUpdates.take(priceUpdates.size).toList()
            }

            // Emit all price updates
            priceUpdates.forEach { update ->
                client.emitPriceUpdate(update)
            }

            // Get collected results
            val receivedUpdates = receivedDeferred.await()

            // Verify all updates were received
            receivedUpdates shouldBe priceUpdates
        }
    }

    /**
     * Test that connect changes state to CONNECTED.
     */
    @Test
    fun `connect changes state to CONNECTED`() = runTest {
        val client = FakeWebSocketClient()

        client.connectionState.value shouldBe ConnectionState.DISCONNECTED

        client.connect()

        client.connectionState.value shouldBe ConnectionState.CONNECTED
        client.connectCalled shouldBe true
    }

    /**
     * Test that disconnect changes state to DISCONNECTED.
     */
    @Test
    fun `disconnect changes state to DISCONNECTED`() = runTest {
        val client = FakeWebSocketClient()
        client.connect()

        client.disconnect()

        client.connectionState.value shouldBe ConnectionState.DISCONNECTED
        client.disconnectCalled shouldBe true
    }

    /**
     * Test that subscribe adds coin IDs to subscribed set.
     */
    @Test
    fun `subscribe adds coin IDs`() = runTest {
        checkAll(100, TestGenerators.coinIdListArb()) { coinIds ->
            val client = FakeWebSocketClient()
            client.connect()

            client.subscribe(coinIds)

            client.subscribedCoinIds shouldContainAll coinIds
        }
    }

    /**
     * Test that unsubscribe removes coin IDs from subscribed set.
     */
    @Test
    fun `unsubscribe removes coin IDs`() = runTest {
        checkAll(100, TestGenerators.coinIdListArb()) { coinIds ->
            val client = FakeWebSocketClient()
            client.connect()
            client.subscribe(coinIds)

            client.unsubscribe(coinIds)

            coinIds.forEach { coinId ->
                client.subscribedCoinIds.contains(coinId) shouldBe false
            }
        }
    }

    /**
     * Test that single price update is received correctly.
     */
    @Test
    fun `single price update is received`() = runTest(UnconfinedTestDispatcher()) {
        val client = FakeWebSocketClient()
        client.connect()

        val expectedUpdate = PriceUpdate(
            coinId = "bitcoin",
            price = 50000.0,
            previousPrice = 49000.0,
            timestamp = 1700000000000L
        )

        val receivedDeferred = async {
            client.priceUpdates.take(1).toList().first()
        }

        client.emitPriceUpdate(expectedUpdate)

        val received = receivedDeferred.await()
        received shouldBe expectedUpdate
    }
}
