package com.example.cryptovault.app.core.util

import androidx.compose.ui.graphics.Color
import com.example.cryptovault.theme.CryptoColors

fun getPriceChangeColor(change: Double, colors: CryptoColors): Color {
    return when {
        change > 0 -> colors.profit      // emerald-400 (#34D399)
        change < 0 -> colors.loss        // rose-400 (#FB7185)
        else -> colors.neutral           // slate-400
    }
}

fun getPriceChangeColorHex(change: Double): Long {
    return when {
        change > 0 -> 0xFF34D399  // emerald-400
        change < 0 -> 0xFFFB7185  // rose-400
        else -> 0xFF94A3B8        // slate-400 (neutral)
    }
}

enum class PriceChangeDirection {
    POSITIVE,
    NEGATIVE,
    NEUTRAL
}

fun getPriceChangeDirection(change: Double): PriceChangeDirection {
    return when {
        change > 0 -> PriceChangeDirection.POSITIVE
        change < 0 -> PriceChangeDirection.NEGATIVE
        else -> PriceChangeDirection.NEUTRAL
    }
}
