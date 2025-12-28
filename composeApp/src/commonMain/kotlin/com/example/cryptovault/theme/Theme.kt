/**
 * Theme.kt
 *
 * Main theme configuration file for the CryptoVault application.
 * This file sets up the Material 3 theme and provides all design system
 * tokens through CompositionLocal providers.
 *
 * The theme supports:
 * - Automatic dark/light mode based on system settings
 * - Material 3 color scheme integration
 * - Custom CryptoVault design tokens (colors, typography, spacing, shapes)
 * - Legacy color palette support for backward compatibility
 * - Accessibility settings
 *
 * Usage:
 * ```kotlin
 * CoinRoutineTheme {
 *     // Your app content here
 *     // Access theme values via LocalCryptoColors.current, etc.
 * }
 * ```
 *
 * @see CoinRoutineTheme for the main theme composable
 * @see LocalCryptoColors for color tokens
 * @see LocalCryptoTypography for typography tokens
 * @see LocalCryptoSpacing for spacing tokens
 * @see LocalCryptoShapes for shape tokens
 */
package com.example.cryptovault.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Material 3 light color scheme for CryptoVault.
 *
 * Maps the app's color palette to Material 3 color roles for
 * proper integration with Material components.
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

/**
 * Material 3 dark color scheme for CryptoVault.
 *
 * Maps the app's color palette to Material 3 color roles for
 * proper integration with Material components. This is the
 * primary theme used by the application.
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)

/**
 * Main theme composable for the CryptoVault application.
 *
 * This function sets up the complete theming system including:
 * - Material 3 color scheme (dark/light based on system preference)
 * - Legacy CoinRoutine color palette (for backward compatibility)
 * - Custom CryptoVault design tokens (colors, typography, spacing, shapes)
 * - Accessibility settings
 *
 * All theme values are provided via CompositionLocal, making them
 * accessible throughout the composable tree.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system preference.
 * @param content The composable content to be themed.
 *
 * Example usage:
 * ```kotlin
 * CoinRoutineTheme(darkTheme = true) {
 *     val colors = LocalCryptoColors.current
 *     val typography = LocalCryptoTypography.current
 *     // Build your UI
 * }
 * ```
 */
@Composable
internal fun CoinRoutineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    // Legacy color palette (for backward compatibility)
    val coinRoutineColorsPalette = if (darkTheme) DarkCoinRoutineColorsPalette else LightCoinRoutineColorsPalette
    
    // New design system tokens
    val cryptoColors = if (darkTheme) DarkCryptoColors else LightCryptoColors
    val cryptoTypography = DefaultCryptoTypography
    val cryptoSpacing = DefaultCryptoSpacing
    val cryptoShapes = DefaultCryptoShapes

    CompositionLocalProvider(
        // Legacy
        LocalCoinRoutineColorsPalette provides coinRoutineColorsPalette,
        // New design system
        LocalCryptoColors provides cryptoColors,
        LocalCryptoTypography provides cryptoTypography,
        LocalCryptoSpacing provides cryptoSpacing,
        LocalCryptoShapes provides cryptoShapes,
        LocalCryptoAccessibility provides CryptoAccessibility(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}