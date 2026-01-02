/**
 * Theme.kt
 *
 * Main theme configuration file for the CryptoVault application.
 * This file sets up the Material 3 dark theme for the app.
 *
 * CryptoVault is a dark-theme-only application, optimized for:
 * - Financial data visualization
 * - Reduced eye strain during extended use
 * - Premium, modern aesthetic
 * - Better OLED display efficiency
 *
 * The theme provides:
 * - Material 3 dark color scheme
 * - Custom CryptoVault design tokens (colors, typography, spacing, shapes)
 * - Responsive dimensions that adapt to screen size
 * - Accessibility settings
 *
 * Usage:
 * ```kotlin
 * CoinRoutineTheme {
 *     // Your app content here
 *     // Access theme values via LocalCryptoColors.current, AppTheme.dimensions, etc.
 * }
 * ```
 *
 * @see CoinRoutineTheme for the main theme composable
 * @see LocalCryptoColors for color tokens
 * @see LocalCryptoTypography for typography tokens
 * @see LocalCryptoSpacing for spacing tokens
 * @see LocalCryptoShapes for shape tokens
 * @see LocalDimensions for responsive dimensions
 * @see AppTheme for convenient theme accessors
 */
package com.example.cryptovault.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Material 3 dark color scheme for CryptoVault.
 *
 * Maps the app's color palette to Material 3 color roles for
 * proper integration with Material components.
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
 * CompositionLocal for providing responsive dimensions throughout the app.
 *
 * Provides access to dimension values that automatically adapt to screen size.
 * Default value is medium phone dimensions as a fallback.
 */
val LocalDimensions = staticCompositionLocalOf {
    // Default fallback dimensions (medium phone)
    createMediumPhoneDimensions()
}

/**
 * CompositionLocal for providing use window size class.
 *
 * Provides access to the current window size class.
 */
val LocalWindowSize = staticCompositionLocalOf {
    WindowSize.COMPACT
}

/**
 * Convenient accessor object for theme values.
 *
 * Provides a clean API for accessing theme tokens in composables.
 *
 * Usage:
 * ```kotlin
 * val dimensions = AppTheme.dimensions
 * val spacing = dimensions.screenPadding
 * ```
 */
object AppTheme {
    /**
     * Access responsive dimensions for the current screen size.
     *
     * Dimensions automatically update when screen configuration changes
     * (e.g., rotation, window resize).
     */
    val dimensions: Dimensions
        @Composable
        get() = LocalDimensions.current

    /**
     * Access the current window size class.
     */
    val windowSize: WindowSize
        @Composable
        get() = LocalWindowSize.current
}

/**
 * Main theme composable for the CryptoVault application.
 *
 * This function sets up the complete dark theming system including:
 * - Material 3 dark color scheme (always dark, ignores system preference)
 * - Legacy CoinRoutine color palette (for backward compatibility)
 * - Custom CryptoVault design tokens (colors, typography, spacing, shapes)
 * - Responsive dimensions that adapt to screen size
 * - Accessibility settings
 *
 * All theme values are provided via CompositionLocal, making them
 * accessible throughout the composable tree.
 *
 * @param content The composable content to be themed.
 *
 * Example usage:
 * ```kotlin
 * CoinRoutineTheme {
 *     val colors = LocalCryptoColors.current
 *     val typography = LocalCryptoTypography.current
 *     val dimensions = AppTheme.dimensions
 *     // Build your UI
 * }
 * ```
 */
@Composable
internal fun CoinRoutineTheme(
    content: @Composable () -> Unit
) {
    // Always use dark theme
    val colorScheme = DarkColorScheme
    
    // Legacy color palette (for backward compatibility) - always dark
    val coinRoutineColorsPalette = DarkCoinRoutineColorsPalette
    
    // New design system tokens - always dark
    val cryptoColors = DarkCryptoColors
    val cryptoTypography = DefaultCryptoTypography
    val cryptoSpacing = DefaultCryptoSpacing
    val cryptoShapes = DefaultCryptoShapes
    
    BoxWithConstraints {
        val screenWidthDp = maxWidth.value.toInt()
        
        // Responsive dimensions
        val dimensions = calculateDimensions(screenWidthDp)
        val windowSize = calculateWindowSize(screenWidthDp)

        CompositionLocalProvider(
            // Legacy
            LocalCoinRoutineColorsPalette provides coinRoutineColorsPalette,
            // New design system
            LocalCryptoColors provides cryptoColors,
            LocalCryptoTypography provides cryptoTypography,
            LocalCryptoSpacing provides cryptoSpacing,
            LocalCryptoShapes provides cryptoShapes,
            LocalCryptoAccessibility provides CryptoAccessibility(),
            // Responsive dimensions
            LocalDimensions provides dimensions,
            LocalWindowSize provides windowSize
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                content = content,
            )
        }
    }
}
