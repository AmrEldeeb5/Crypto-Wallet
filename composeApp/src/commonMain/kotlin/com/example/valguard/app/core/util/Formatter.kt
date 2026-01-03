package com.example.valguard.app.core.util




expect fun formatFiat(amount: Double, showDecimal: Boolean = true): String

expect fun formatCoinUnit(amount: Double, symbol: String): String

expect fun formatPercentage(amount: Double): String

/**
 * Formats cryptocurrency amounts with intelligent decimal precision.
 *
 * Uses adaptive decimal places based on amount magnitude:
 * - Large amounts (≥1): 2 decimals (e.g., "1.50")
 * - Medium amounts (≥0.01): 4 decimals (e.g., "0.0500")
 * - Small amounts (<0.01): Up to 8 decimals, trailing zeros trimmed (e.g., "0.00000123")
 *
 * @param maxDecimals Maximum decimal places for very small amounts (default: 8)
 * @return Formatted string representation of the amount
 *
 * @example
 * ```kotlin
 * 1.5.formatCrypto()      // "1.50"
 * 0.05.formatCrypto()     // "0.0500"
 * 0.00000123.formatCrypto() // "0.00000123"
 * ```
 */
fun Double.formatCrypto(maxDecimals: Int = 8): String {
    return when {
        this >= 1.0 -> String.format("%.2f", this)
        this >= 0.01 -> String.format("%.4f", this)
        else -> String.format("%.${maxDecimals}f", this).trimEnd('0').trimEnd('.')
    }
}