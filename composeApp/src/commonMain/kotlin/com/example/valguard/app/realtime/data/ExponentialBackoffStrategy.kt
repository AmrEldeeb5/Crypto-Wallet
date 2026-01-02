package com.example.valguard.app.realtime.data

import com.example.valguard.app.realtime.domain.ReconnectionStrategy
import kotlin.math.min
import kotlin.math.pow

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
