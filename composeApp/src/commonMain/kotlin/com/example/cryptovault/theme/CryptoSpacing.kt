/**
 * CryptoSpacing.kt
 *
 * Defines the spacing system for the CryptoVault application.
 * Provides consistent spacing values based on a 4dp base unit,
 * ensuring visual rhythm and alignment across the app.
 *
 * Spacing scale:
 * - xxs: 4dp  - Minimal spacing (icon padding, tight gaps)
 * - xs:  8dp  - Extra small (between related elements)
 * - sm:  12dp - Small (standard component padding)
 * - md:  16dp - Medium (section spacing, card padding)
 * - lg:  24dp - Large (between sections)
 * - xl:  32dp - Extra large (major section breaks)
 * - xxl: 48dp - Maximum (screen margins, hero spacing)
 *
 * @see DefaultCryptoSpacing for the default spacing configuration
 * @see LocalCryptoSpacing for accessing spacing via CompositionLocal
 */
package com.example.cryptovault.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data class containing all spacing values for the CryptoVault design system.
 *
 * This immutable class provides consistent spacing values for margins,
 * padding, and gaps. Use [LocalCryptoSpacing] to access these values
 * in Composable functions.
 *
 * @property xxs Extra extra small spacing (4dp) - for tight gaps
 * @property xs Extra small spacing (8dp) - for related elements
 * @property sm Small spacing (12dp) - for component padding
 * @property md Medium spacing (16dp) - for standard padding
 * @property lg Large spacing (24dp) - for section gaps
 * @property xl Extra large spacing (32dp) - for major breaks
 * @property xxl Extra extra large spacing (48dp) - for screen margins
 */
@Immutable
data class CryptoSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp
)

/**
 * Default spacing configuration for CryptoVault.
 *
 * Based on a 4dp base unit, providing a harmonious spacing scale
 * that works well across different screen sizes.
 */
val DefaultCryptoSpacing = CryptoSpacing()

/**
 * CompositionLocal for accessing [CryptoSpacing] throughout the app.
 *
 * Usage in Composable functions:
 * ```kotlin
 * val spacing = LocalCryptoSpacing.current
 * Spacer(modifier = Modifier.height(spacing.md))
 * ```
 *
 * The value is provided by [CoinRoutineTheme] and defaults to [DefaultCryptoSpacing].
 */
val LocalCryptoSpacing = compositionLocalOf { DefaultCryptoSpacing }
