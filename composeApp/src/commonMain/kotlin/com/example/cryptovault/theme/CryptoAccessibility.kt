/**
 * CryptoAccessibility.kt
 *
 * Defines accessibility settings for the CryptoVault application.
 * Provides configuration options that help make the app more accessible
 * to users with different needs and preferences.
 *
 * Current settings:
 * - reduceMotion: Respects user preference for reduced animations
 *
 * These settings can be used throughout the app to conditionally
 * enable/disable animations and other motion-based UI elements.
 *
 * @see LocalCryptoAccessibility for accessing settings via CompositionLocal
 */
package com.example.cryptovault.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Data class containing accessibility configuration for CryptoVault.
 *
 * Use [LocalCryptoAccessibility] to access these settings in Composable functions.
 *
 * @property reduceMotion When true, animations should be minimized or disabled.
 *                        Respects the user's system-level accessibility preference.
 *
 * Example usage:
 * ```kotlin
 * val accessibility = LocalCryptoAccessibility.current
 * if (!accessibility.reduceMotion) {
 *     // Apply animation
 * }
 * ```
 */
data class CryptoAccessibility(
    val reduceMotion: Boolean = false
)

/**
 * CompositionLocal for accessing [CryptoAccessibility] throughout the app.
 *
 * The value is provided by [CoinRoutineTheme] and defaults to standard settings.
 */
val LocalCryptoAccessibility = compositionLocalOf { CryptoAccessibility() }
