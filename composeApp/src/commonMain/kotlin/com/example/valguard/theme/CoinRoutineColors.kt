/**
 * CoinRoutineColors.kt
 *
 * Legacy color palette for backward compatibility with older components.
 * This file provides the original profit/loss color scheme used before
 * the CryptoColors design system was introduced.
 *
 * Colors provided:
 * - profitGreen: Green color for positive price changes (#32de84)
 * - lossRed: Red color for negative price changes (#D2122E)
 *
 * Note: New components should use [CryptoColors] instead. This palette
 * is maintained for components that haven't been migrated yet.
 *
 * @see CryptoColors for the new design system colors
 * @see LocalCoinRoutineColorsPalette for accessing via CompositionLocal
 */
package com.example.valguard.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Legacy color palette data class for profit/loss indicators.
 *
 * @property profitGreen Color for positive price changes (green)
 * @property lossRed Color for negative price changes (red)
 *
 * @deprecated Use [CryptoColors.profit] and [CryptoColors.loss] instead.
 */
@Immutable
data class CoinRoutineColorsPalette(
    val profitGreen: Color = Color.Unspecified,
    val lossRed: Color = Color.Unspecified,
)

/** Profit green color for light theme (#32de84 - mint green) */
val ProfitGreenColor = Color(color = 0xFF32de84)

/** Loss red color for light theme (#D2122E - crimson red) */
val LossRedColor = Color(color = 0xFFD2122E)

/** Profit green color for dark theme (same as light for consistency) */
val DarkProfitGreenColor = Color(color = 0xFF32de84)

/** Loss red color for dark theme (same as light for consistency) */
val DarkLossRedColor = Color(color = 0xFFD2122E)

/**
 * Light theme color palette instance.
 */
val LightCoinRoutineColorsPalette = CoinRoutineColorsPalette(
    profitGreen = ProfitGreenColor,
    lossRed = LossRedColor,
)

/**
 * Dark theme color palette instance.
 */
val DarkCoinRoutineColorsPalette = CoinRoutineColorsPalette(
    profitGreen = DarkProfitGreenColor,
    lossRed = DarkLossRedColor,
)

/**
 * CompositionLocal for accessing [CoinRoutineColorsPalette] throughout the app.
 *
 * Usage in Composable functions:
 * ```kotlin
 * val colors = LocalCoinRoutineColorsPalette.current
 * Text(color = if (isProfit) colors.profitGreen else colors.lossRed)
 * ```
 *
 * @deprecated Use [LocalCryptoColors] instead for new components.
 */
val LocalCoinRoutineColorsPalette = compositionLocalOf { CoinRoutineColorsPalette() }