package com.example.cryptowallet.app.realtime.data

import com.example.cryptowallet.app.realtime.domain.ReconnectionStrategy
import kotlin.math.min
import kotlin.math.pow

/**
 * Implements exponential backoff for reconnection attempts.
 *
 * The delay follows the formula: delay = initialDelayMs × (multiplier ^ attemptNumber)
 * with a maximum cap to prevent excessively long delays.
 *
 * @param initialDelayMs Initial delay in milliseconds (default: 1000ms)
 * @param maxAttempts Maximum number of attempts before falling back (default: 3)
 * @param multiplier Multiplier for exponential growth (default: 2.0)
 * @param maxDelayMs Maximum delay cap in milliseconds (default: 30000ms)
 */
class ExponentialBackoffStrategy(
    private val initialDelayMs: Long = 1000L,
    private val maxAttempts: Int = 3,
    private val multiplier: Double = 2.0,
    private val maxDelayMs: Long = 30000L
) : ReconnectionStrategy {

    override fun nextDelay(attemptNumber: Int): Long {
        val calculatedDelay = (initialDelayMs * multiplier.pow(attemptNumber.toDouble())).toLong()
        return min(calculatedDelay, maxDelayMs)
    }

    override fun shouldFallback(attemptNumber: Int): Boolean {
        return attemptNumber >= maxAttempts
    }

    override fun reset() {
        // No state to reset in this implementation
        // Could be extended to track attempt count internally
    }
}
