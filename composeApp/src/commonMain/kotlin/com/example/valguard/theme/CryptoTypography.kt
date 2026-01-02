/**
 * CryptoTypography.kt
 *
 * Defines the typography system for the Valguard application.
 * This file contains text styles organized by their semantic purpose,
 * following Material Design 3 typography guidelines.
 *
 * Typography categories:
 * - Display: Large prominent text (portfolio values, hero sections)
 * - Title: Section headers and coin names
 * - Body: Primary content and descriptions
 * - Label: Buttons, chips, and interactive elements
 * - Caption: Small annotations and timestamps
 *
 * All styles use the system default font family but can be customized
 * by modifying [DefaultCryptoTypography] or providing custom fonts.
 *
 * @see DefaultCryptoTypography for the default typography configuration
 * @see LocalCryptoTypography for accessing typography via CompositionLocal
 */
package com.example.valguard.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Data class containing all typography styles for the Valguard design system.
 *
 * This immutable class provides consistent text styling across the app.
 * Use [LocalCryptoTypography] to access these styles in Composable functions.
 *
 * @property displayLarge Largest display text (36sp) - for main portfolio values
 * @property displayMedium Medium display text (28sp) - for section highlights
 * @property titleLarge Large title (22sp) - for screen titles
 * @property titleMedium Medium title (18sp) - for card headers
 * @property titleSmall Small title (14sp) - for list item titles
 * @property bodyLarge Large body text (16sp) - for primary content
 * @property bodyMedium Medium body text (14sp) - for standard content
 * @property bodySmall Small body text (12sp) - for secondary content
 * @property labelLarge Large label (14sp) - for primary buttons
 * @property labelMedium Medium label (12sp) - for chips and tags
 * @property labelSmall Small label (11sp) - for small buttons
 * @property caption Caption text (10sp) - for timestamps and annotations
 */
@Immutable
data class CryptoTypography(
    // Display styles - for large prominent text like portfolio value
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    
    // Title styles - for section headers and coin names
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    
    // Body styles - for primary and secondary content
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    
    // Label styles - for buttons, chips, and tags
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    
    // Caption - for small annotations and timestamps
    val caption: TextStyle
)

/**
 * Default typography configuration for Valguard.
 *
 * Uses the system default font family with carefully tuned font sizes,
 * weights, line heights, and letter spacing for optimal readability
 * in a financial/crypto application context.
 */
val DefaultCryptoTypography = CryptoTypography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )
)

/**
 * CompositionLocal for accessing [CryptoTypography] throughout the app.
 *
 * Usage in Composable functions:
 * ```kotlin
 * val typography = LocalCryptoTypography.current
 * Text(style = typography.titleMedium)
 * ```
 *
 * The value is provided by [CoinRoutineTheme] and defaults to [DefaultCryptoTypography].
 */
val LocalCryptoTypography = compositionLocalOf { DefaultCryptoTypography }
