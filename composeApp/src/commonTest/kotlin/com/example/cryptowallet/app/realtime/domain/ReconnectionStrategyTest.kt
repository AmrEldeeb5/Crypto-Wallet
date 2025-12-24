package com.example.cryptowallet.app.realtime.domain

import com.example.cryptowallet.app.realtime.data.ExponentialBackoffStrategy
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test

/**
 * Property-based tests for ReconnectionStrategy.
 */
class ReconnectionStrategyTest {

    /**
     * **Feature: realtime-price-updates, Property 6: Exponential backoff delay calculation**
     *
     * For any sequence of reconnection attempts, the delay between attempts SHALL follow
     * the exponential backoff formula: delay = initialDelay × (multiplier ^ attemptNumber).
     *
     * Validates: Requirements 3.1
     */
    @Test
    fun `Property 6 - exponential backoff delay calculation`() = runTest {
        val initialDelayArb = Arb.long(min = 100L, max = 5000L)
        val multiplierValues = listOf(1.5, 2.0, 2.5, 3.0)
        val attemptArb = Arb.int(min = 0, max = 10)

        checkAll(100, initialDelayArb, attemptArb) { initialDelay, attemptNumber ->
            multiplierValues.forEach { multiplier ->
                val maxDelay = 30000L
                val strategy = ExponentialBackoffStrategy(
                    initialDelayMs = initialDelay,
                    multiplier = multiplier,
                    maxDelayMs = maxDelay
                )

                val actualDelay = strategy.nextDelay(attemptNumber)
                val expectedDelay = min(
                    (initialDelay * multiplier.pow(attemptNumber.toDouble())).toLong(),
                    maxDelay
                )

                actualDelay shouldBe expectedDelay
            }
        }
    }

    /**
     * Test that delay is always at least the initial delay for attempt 0.
     */
    @Test
    fun `first attempt delay equals initial delay`() = runTest {
        val initialDelayArb = Arb.long(min = 100L, max = 10000L)

        checkAll(100, initialDelayArb) { initialDelay ->
            val strategy = ExponentialBackoffStrategy(initialDelayMs = initialDelay)
            strategy.nextDelay(0) shouldBe initialDelay
        }
    }

    /**
     * Test that delay increases with each attempt (until max).
     */
    @Test
    fun `delay increases with each attempt until max`() = runTest {
        val initialDelayArb = Arb.long(min = 100L, max = 1000L)

        checkAll(100, initialDelayArb) { initialDelay ->
            val maxDelay = 30000L
            val strategy = ExponentialBackoffStrategy(
                initialDelayMs = initialDelay,
                maxDelayMs = maxDelay
            )

            var previousDelay = 0L
            for (attempt in 0..5) {
                val currentDelay = strategy.nextDelay(attempt)
                currentDelay shouldBeGreaterThanOrEqual previousDelay
                currentDelay shouldBeLessThanOrEqual maxDelay
                previousDelay = currentDelay
            }
        }
    }

    /**
     * Test that shouldFallback returns true after maxAttempts.
     */
    @Test
    fun `shouldFallback returns true after maxAttempts`() = runTest {
        val maxAttemptsArb = Arb.int(min = 1, max = 10)

        checkAll(100, maxAttemptsArb) { maxAttempts ->
            val strategy = ExponentialBackoffStrategy(maxAttempts = maxAttempts)

            // Before max attempts - should not fallback
            for (attempt in 0 until maxAttempts) {
                strategy.shouldFallback(attempt) shouldBe false
            }

            // At and after max attempts - should fallback
            strategy.shouldFallback(maxAttempts) shouldBe true
            strategy.shouldFallback(maxAttempts + 1) shouldBe true
        }
    }

    /**
     * Test that delay is capped at maxDelayMs.
     */
    @Test
    fun `delay is capped at maxDelayMs`() = runTest {
        val maxDelayArb = Arb.long(min = 1000L, max = 60000L)

        checkAll(100, maxDelayArb) { maxDelay ->
            val strategy = ExponentialBackoffStrategy(
                initialDelayMs = 1000L,
                multiplier = 10.0, // Large multiplier to quickly exceed max
                maxDelayMs = maxDelay
            )

            // High attempt number should still be capped
            val delay = strategy.nextDelay(10)
            delay shouldBeLessThanOrEqual maxDelay
        }
    }

    /**
     * Test default configuration values.
     */
    @Test
    fun `default configuration produces expected delays`() = runTest {
        val strategy = ExponentialBackoffStrategy()

        // Default: initialDelay=1000, multiplier=2.0, maxAttempts=3
        strategy.nextDelay(0) shouldBe 1000L  // 1000 * 2^0 = 1000
        strategy.nextDelay(1) shouldBe 2000L  // 1000 * 2^1 = 2000
        strategy.nextDelay(2) shouldBe 4000L  // 1000 * 2^2 = 4000

        strategy.shouldFallback(0) shouldBe false
        strategy.shouldFallback(1) shouldBe false
        strategy.shouldFallback(2) shouldBe false
        strategy.shouldFallback(3) shouldBe true
    }
}
