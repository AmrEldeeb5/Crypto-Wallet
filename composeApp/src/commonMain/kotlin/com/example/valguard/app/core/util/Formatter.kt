package com.example.valguard.app.core.util

import kotlin.math.round

expect fun formatFiat(amount: Double, showDecimal: Boolean = true): String

expect fun formatCoinUnit(amount: Double, symbol: String): String

expect fun formatPercentage(amount: Double): String

/**
 * Formats large numbers with compact notation using abbreviations.
 *
 * Uses K (thousands), M (millions), and B (billions) suffixes:
 * - Billions (≥1B): "1.23B"
 * - Millions (≥1M): "456.78M"
 * - Thousands (≥1K): "12.34K"
 * - Below 1K: "$1,234"
 *
 * @param amount The number to format
 * @return Formatted string with currency symbol and abbreviation
 *
 * @example
 * ```kotlin
 * formatCompactNumber(1234567890.0)  // "$1.23B"
 * formatCompactNumber(456780000.0)   // "$456.78M"
 * formatCompactNumber(12340.0)       // "$12.34K"
 * formatCompactNumber(999.0)         // "$999"
 * ```
 */
fun formatCompactNumber(amount: Double): String {
    val absAmount = kotlin.math.abs(amount)
    val sign = if (amount < 0) "-" else ""
    
    return when {
        absAmount >= 1_000_000_000 -> {
            val billions = absAmount / 1_000_000_000
            "$sign$${String.format("%.2f", billions)}B"
        }
        absAmount >= 1_000_000 -> {
            val millions = absAmount / 1_000_000
            "$sign$${String.format("%.2f", millions)}M"
        }
        absAmount >= 1_000 -> {
            val thousands = absAmount / 1_000
            "$sign$${String.format("%.2f", thousands)}K"
        }
        else -> {
            "$sign$${absAmount.toInt()}"
        }
    }
}

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
    val decimals = when {
        this >= 1.0 -> 2
        this >= 0.01 -> 4
        else -> maxDecimals
    }
    
    // Calculate 10^decimals without using pow
    var factor = 1.0
    repeat(decimals) { factor *= 10.0 }
    
    val rounded = round(this * factor) / factor
    val formatted = rounded.toString()
    
    // Handle the decimal part
    val parts = formatted.split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) parts[1] else ""
    
    return when {
        this >= 0.01 -> {
            // Fixed decimal places
            val paddedDec = decPart.padEnd(decimals, '0').take(decimals)
            "$intPart.$paddedDec"
        }
        else -> {
            // Trim trailing zeros for small amounts
            val paddedDec = decPart.padEnd(decimals, '0').take(decimals)
            "$intPart.$paddedDec".trimEnd('0').trimEnd('.')
        }
    }
}
