/**
 * Dimensions.kt
 *
 * Responsive dimension system for Valguard application.
 * Provides adaptive sizing for UI elements based on screen size categories.
 *
 * The system categorizes screens into four sizes:
 * - Small phones: < 360dp width
 * - Medium phones: 360-411dp width
 * - Large phones: 411-600dp width
 * - Tablets: > 600dp width
 *
 * All dimension values are calculated based on screen configuration and
 * cached using Compose's remember mechanism for optimal performance.
 *
 * **Text Sizing**: This system uses Material 3 Typography tokens for text sizing.
 * Use `MaterialTheme.typography` for all text styles instead of custom sizes.
 * This provides better consistency and automatic scaling across screen sizes.
 *
 * Usage:
 * ```kotlin
 * val dimensions = AppTheme.dimensions
 * Text(
 *     text = "Hello",
 *     style = MaterialTheme.typography.headlineMedium,
 *     modifier = Modifier.padding(dimensions.screenPadding)
 * )
 * ```
 *
 * @see calculateDimensions for dimension calculation
 * @see WindowSize for screen size classification
 */
package com.example.valguard.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Immutable data class containing all responsive dimension values.
 *
 * Note: Text sizes are handled by Material 3 Typography tokens.
 * Use MaterialTheme.typography for text styling instead of custom sizes.
 *
 * @property screenPadding Outer padding for screen edges
 * @property cardPadding Internal padding for cards
 * @property itemSpacing Spacing between items in lists/grids
 * @property verticalSpacing Vertical spacing between sections
 * @property smallSpacing Tight spacing for closely related elements
 * @property coinIconSize Size for cryptocurrency icons
 * @property appIconSize Size for app logo/icon
 * @property buttonHeight Height for primary buttons
 * @property cardCornerRadius Corner radius for cards
 * @property cardElevation Elevation for cards
 * @property coinCardMinWidth Minimum width for coin selection cards
 * @property gridColumns Number of columns for coin selection grid
 */
data class Dimensions(
    // Spacing
    val screenPadding: Dp,
    val cardPadding: Dp,
    val itemSpacing: Dp,
    val verticalSpacing: Dp,
    val smallSpacing: Dp,
    
    // Icon sizes
    val coinIconSize: Dp,
    val appIconSize: Dp,
    
    // Component sizes
    val buttonHeight: Dp,
    val cardCornerRadius: Dp,
    val cardElevation: Dp,
    
    // Grid
    val coinCardMinWidth: Dp,
    val gridColumns: Int
)

/**
 * Window size classification following Material Design 3 guidelines.
 *
 * @property COMPACT Screens < 600dp width (phones in portrait)
 * @property MEDIUM Screens 600-840dp width (tablets in portrait, phones in landscape)
 * @property EXPANDED Screens > 840dp width (tablets in landscape, desktops)
 */
enum class WindowSize {
    COMPACT,    // < 600dp width
    MEDIUM,     // 600dp - 840dp width
    EXPANDED    // > 840dp width
}

/**
 * Determines the current window size class based on screen width.
 *
 * Uses Material Design 3 breakpoints to classify the screen size.
 * This classification can be used for responsive layout decisions.
 *
 * @param screenWidthDp The width of the screen in DP
 * @return WindowSize enum value representing the current screen size class
 */
fun calculateWindowSize(screenWidthDp: Int): WindowSize {
    return when {
        screenWidthDp < 600 -> WindowSize.COMPACT
        screenWidthDp < 840 -> WindowSize.MEDIUM
        else -> WindowSize.EXPANDED
    }
}

/**
 * Calculates responsive dimension values based on screen configuration.
 *
 * Dimension values are selected based on screen width breakpoints:
 * - < 320dp: Extra small phone dimensions (with warning log)
 * - 320-360dp: Small phone dimensions
 * - 360-411dp: Medium phone dimensions
 * - 411-600dp: Large phone dimensions
 * - 600-1200dp: Tablet dimensions
 * - > 1200dp: Capped at tablet dimensions (with info log)
 *
 * @param screenWidthDp The width of the screen in DP
 * @return Dimensions object containing all responsive sizing values
 */
fun calculateDimensions(screenWidthDp: Int): Dimensions {
    return when {
        // Extremely small screens (< 320dp) - fallback to small phone dimensions
        screenWidthDp < 320 -> {
            println("⚠️ Valguard: Screen width ${screenWidthDp}dp is below minimum supported size (320dp). Using small phone dimensions.")
            createSmallPhoneDimensions()
        }
        // Small phones (320-360dp)
        screenWidthDp < 360 -> createSmallPhoneDimensions()
        // Medium phones (360-411dp)
        screenWidthDp < 411 -> createMediumPhoneDimensions()
        // Large phones (411-600dp)
        screenWidthDp < 600 -> createLargePhoneDimensions()
        // Tablets (600-1200dp)
        screenWidthDp < 1200 -> createTabletDimensions()
        // Extremely large screens (> 1200dp) - cap at tablet dimensions
        else -> {
            println("ℹ️ Valguard: Screen width ${screenWidthDp}dp exceeds typical tablet size. Capping dimensions at tablet values.")
            createTabletDimensions()
        }
    }
}

/**
 * Creates dimension values optimized for small phones (< 360dp width).
 *
 * These dimensions prioritize content density and ensure minimum
 * touch target sizes are maintained even on the smallest screens.
 *
 * Target devices: 4.7" - 5.5" phones (320dp - 360dp width)
 *
 * @return Dimensions configured for small phones
 */
internal fun createSmallPhoneDimensions() = Dimensions(
    screenPadding = 16.dp,
    cardPadding = 12.dp,
    itemSpacing = 8.dp,
    verticalSpacing = 12.dp,
    smallSpacing = 4.dp,
    
    coinIconSize = 36.dp,
    appIconSize = 48.dp,
    
    buttonHeight = 48.dp,
    cardCornerRadius = 12.dp,
    cardElevation = 3.dp,
    
    coinCardMinWidth = 130.dp,
    gridColumns = 2
)

/**
 * Creates dimension values optimized for medium phones (360-411dp width).
 *
 * These are the baseline dimensions, representing the most common
 * phone screen sizes. All other dimension sets scale relative to these.
 *
 * Target devices: 5.5" - 6.1" phones (360dp - 411dp width)
 *
 * @return Dimensions configured for medium phones
 */
internal fun createMediumPhoneDimensions() = Dimensions(
    screenPadding = 16.dp,
    cardPadding = 14.dp,
    itemSpacing = 10.dp,
    verticalSpacing = 14.dp,
    smallSpacing = 6.dp,
    
    coinIconSize = 40.dp,
    appIconSize = 56.dp,
    
    buttonHeight = 50.dp,
    cardCornerRadius = 14.dp,
    cardElevation = 4.dp,
    
    coinCardMinWidth = 140.dp,
    gridColumns = 2
)

/**
 * Creates dimension values optimized for large phones (411-600dp width).
 *
 * These dimensions provide more generous spacing and larger text
 * to take advantage of the additional screen real estate.
 *
 * Target devices: 6.1" - 6.7" phones (411dp - 428dp width)
 *
 * @return Dimensions configured for large phones
 */
internal fun createLargePhoneDimensions() = Dimensions(
    screenPadding = 20.dp,
    cardPadding = 16.dp,
    itemSpacing = 12.dp,
    verticalSpacing = 16.dp,
    smallSpacing = 8.dp,
    
    coinIconSize = 44.dp,
    appIconSize = 64.dp,
    
    buttonHeight = 52.dp,
    cardCornerRadius = 16.dp,
    cardElevation = 4.dp,
    
    coinCardMinWidth = 150.dp,
    gridColumns = 2
)

/**
 * Creates dimension values optimized for tablets (> 600dp width).
 *
 * These dimensions provide maximum spacing and text sizes, and
 * enable a 3-column grid layout for coin selection.
 *
 * Target devices: 7" tablets and above
 *
 * @return Dimensions configured for tablets
 */
internal fun createTabletDimensions() = Dimensions(
    screenPadding = 32.dp,
    cardPadding = 20.dp,
    itemSpacing = 18.dp,
    verticalSpacing = 28.dp,
    smallSpacing = 12.dp,
    
    coinIconSize = 72.dp,
    appIconSize = 96.dp,
    
    buttonHeight = 60.dp,
    cardCornerRadius = 18.dp,
    cardElevation = 6.dp,
    
    coinCardMinWidth = 180.dp,
    gridColumns = 3
)

/**
 * Calculates a responsive size value based on window size class.
 *
 * Provides different Dp values for compact, medium, and expanded screens.
 * If medium or expanded values are not provided, they default to the
 * previous size class value.
 *
 * @param compact Value for compact screens (< 600dp)
 * @param medium Value for medium screens (600-840dp), defaults to compact
 * @param expanded Value for expanded screens (> 840dp), defaults to medium
 * @return Dp value appropriate for current screen size
 */
@Composable
fun responsiveSize(
    compact: Dp,
    medium: Dp = compact,
    expanded: Dp = medium
): Dp {
    return when (LocalWindowSize.current) {
        WindowSize.COMPACT -> compact
        WindowSize.MEDIUM -> medium
        WindowSize.EXPANDED -> expanded
    }
}
