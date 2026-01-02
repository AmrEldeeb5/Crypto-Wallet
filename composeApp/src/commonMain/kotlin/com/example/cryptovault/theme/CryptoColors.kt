/**
 * CryptoColors.kt
 *
 * Defines the custom dark theme color system for the CryptoVault application.
 * This file contains semantic color tokens that provide consistent theming
 * across the entire app.
 *
 * CryptoVault is a dark-theme-only application, optimized for financial
 * data visualization and reduced eye strain.
 *
 * The color system is organized into categories:
 * - Background colors: Primary and secondary backgrounds
 * - Semantic colors: Profit (green), Loss (red), Neutral
 * - Card/Surface colors: For elevated UI elements
 * - Text colors: Primary, secondary, and tertiary text
 * - Accent colors: Blue, Purple, and Pink gradients
 * - Status colors: Connected, Connecting, Error states
 * - Interactive colors: Buttons and interactive elements
 * - Utility colors: Dividers, borders, shimmer effects
 *
 * @see DarkCryptoColors for the dark theme implementation
 * @see LocalCryptoColors for accessing colors via CompositionLocal
 */
package com.example.cryptovault.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// SPLASH SCREEN COLOR TOKENS
// ============================================================================
// Additional Slate colors for splash screen components

/** Slate 950 - Darkest background */
val Slate950 = Color(0xFF020617)
/** Slate 900 - Primary dark background */
val Slate900 = Color(0xFF0F172A)
/** Slate 800 - Elevated surfaces */
val Slate800 = Color(0xFF1E293B)
/** Slate 700 - Borders and dividers */
val Slate700 = Color(0xFF334155)
/** Slate 600 - Muted borders */
val Slate600 = Color(0xFF475569)
/** Slate 500 - Secondary text */
val Slate500 = Color(0xFF64748B)
/** Slate 400 - Body text */
val Slate400 = Color(0xFF94A3B8)
/** Slate 300 - Primary light text */
val Slate300 = Color(0xFFCBD5E1)
/** Slate 200 - Very light text/borders */
val Slate200 = Color(0xFFE2E8F0)

// Blue color variants
/** Blue 700 - Darker blue for gradients */
val Blue700 = Color(0xFF1D4ED8)
/** Blue 600 - Dark blue accent */
val Blue600 = Color(0xFF2563EB)
/** Blue 500 - Medium blue accent */
val Blue500 = Color(0xFF3B82F6)
/** Blue 400 - Light blue accent */
val Blue400 = Color(0xFF60A5FA)

// Purple color variants
/** Purple 700 - Darker purple for gradients */
val Purple700 = Color(0xFF7C3AED)
/** Purple 600 - Dark purple accent */
val Purple600 = Color(0xFF7C3AED)
/** Purple 500 - Medium purple accent */
val Purple500 = Color(0xFFA855F7)
/** Purple 400 - Light purple accent */
val Purple400 = Color(0xFFC084FC)

// Pink color variants
/** Pink 500 - Medium pink accent */
val Pink500 = Color(0xFFEC4899)
/** Pink 400 - Light pink accent */
val Pink400 = Color(0xFFF9A8D4)

// Emerald (Success/Secure) color variants
/** Emerald 500 - Medium emerald for success states */
val Emerald500 = Color(0xFF10B981)
/** Emerald 400 - Light emerald accent */
val Emerald400 = Color(0xFF34D399)

// Yellow (Warning/Real-time) color variants
/** Yellow 400 - Yellow accent for warnings */
val Yellow400 = Color(0xFFFBBF24)

/**
 * Data class containing all color tokens for the CryptoVault design system.
 *
 * This immutable class holds semantic color values that adapt based on the
 * current theme (dark/light). Use [LocalCryptoColors] to access these colors
 * in Composable functions.
 *
 * @property backgroundPrimary Main background color for screens
 * @property backgroundSecondary Secondary background for sections/containers
 * @property profit Color indicating positive price changes or gains (green)
 * @property loss Color indicating negative price changes or losses (red)
 * @property neutral Color for unchanged/neutral states
 * @property cardBackground Background color for card components
 * @property cardBackgroundElevated Background for elevated/highlighted cards
 * @property cardBorder Border color for card components
 * @property textPrimary Primary text color for headings and important content
 * @property textSecondary Secondary text color for supporting content
 * @property textTertiary Tertiary text color for hints and less important text
 * @property accentBlue400 Light blue accent (400 weight)
 * @property accentBlue500 Medium blue accent (500 weight)
 * @property accentBlue600 Dark blue accent (600 weight)
 * @property accentPurple400 Light purple accent (400 weight)
 * @property accentPurple500 Medium purple accent (500 weight)
 * @property accentPurple600 Dark purple accent (600 weight)
 * @property accentPink400 Light pink accent (400 weight)
 * @property accentPink500 Medium pink accent (500 weight)
 * @property statusConnected Color for connected/online status
 * @property statusConnecting Color for connecting/loading status
 * @property statusError Color for error/offline status
 * @property buttonPrimary Primary button background color
 * @property buttonSecondary Secondary button background color
 * @property buttonDisabled Disabled button background color
 * @property divider Color for divider lines
 * @property border Color for borders and outlines
 * @property shimmerBase Base color for shimmer/skeleton loading effect
 * @property shimmerHighlight Highlight color for shimmer animation
 */
@Immutable
data class CryptoColors(
    // Background colors
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    
    // Profit/Loss semantic colors
    val profit: Color,
    val loss: Color,
    val neutral: Color,
    
    // Card and surface colors
    val cardBackground: Color,
    val cardBackgroundElevated: Color,
    val cardBorder: Color,
    
    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    
    // Accent colors - Blue
    val accentBlue400: Color,
    val accentBlue500: Color,
    val accentBlue600: Color,
    
    // Accent colors - Purple
    val accentPurple400: Color,
    val accentPurple500: Color,
    val accentPurple600: Color,
    
    // Accent colors - Pink
    val accentPink400: Color,
    val accentPink500: Color,
    
    // Status colors
    val statusConnected: Color,
    val statusConnecting: Color,
    val statusError: Color,
    
    // Interactive element colors
    val buttonPrimary: Color,
    val buttonSecondary: Color,
    val buttonDisabled: Color,
    
    // Divider and border colors
    val divider: Color,
    val border: Color,
    
    // Shimmer/skeleton colors
    val shimmerBase: Color,
    val shimmerHighlight: Color
)

/**
 * Dark theme color palette for CryptoVault.
 *
 * This is the primary theme of the application, using a slate-based
 * color scheme with emerald for profit and rose for loss indicators.
 * Colors are based on Tailwind CSS color palette for consistency.
 *
 * Color references:
 * - Backgrounds: Slate 950/900
 * - Cards: Slate 800/700
 * - Text: White, Slate 300, Slate 400
 * - Accents: Blue, Purple, Pink (400-600 weights)
 * - Semantic: Emerald (profit), Rose (loss)
 */
val DarkCryptoColors = CryptoColors(
    // Backgrounds - Slate 950/900
    backgroundPrimary = Color(0xFF020617),    // slate-950
    backgroundSecondary = Color(0xFF0F172A),  // slate-900
    
    // Profit/Loss - Emerald/Rose
    profit = Color(0xFF34D399),               // emerald-400
    loss = Color(0xFFFB7185),                 // rose-400
    neutral = Color(0xFF94A3B8),              // slate-400
    
    // Cards - Slate 800 with transparency
    cardBackground = Color(0xFF1E293B),       // slate-800
    cardBackgroundElevated = Color(0xFF334155), // slate-700
    cardBorder = Color(0xFF475569),           // slate-600
    
    // Text
    textPrimary = Color(0xFFFFFFFF),          // white
    textSecondary = Color(0xFFCBD5E1),        // slate-300 (lighter for better contrast)
    textTertiary = Color(0xFF94A3B8),         // slate-400
    
    // Accent - Blue
    accentBlue400 = Color(0xFF60A5FA),        // blue-400
    accentBlue500 = Color(0xFF3B82F6),        // blue-500
    accentBlue600 = Color(0xFF2563EB),        // blue-600
    
    // Accent - Purple
    accentPurple400 = Color(0xFFC084FC),      // purple-400
    accentPurple500 = Color(0xFFA855F7),      // purple-500
    accentPurple600 = Color(0xFF9333EA),      // purple-600
    
    // Accent - Pink
    accentPink400 = Color(0xFFF472B6),        // pink-400
    accentPink500 = Color(0xFFEC4899),        // pink-500
    
    // Status
    statusConnected = Color(0xFF34D399),      // emerald-400
    statusConnecting = Color(0xFF60A5FA),     // blue-400
    statusError = Color(0xFFFB7185),          // rose-400
    
    // Buttons - Use darker blue for better contrast with white text
    buttonPrimary = Color(0xFF1D4ED8),        // blue-700 (darker for contrast)
    buttonSecondary = Color(0xFF1E293B),      // slate-800
    buttonDisabled = Color(0xFF475569),       // slate-600
    
    // Dividers
    divider = Color(0xFF334155),              // slate-700
    border = Color(0xFF475569),               // slate-600
    
    // Shimmer
    shimmerBase = Color(0xFF1E293B),          // slate-800
    shimmerHighlight = Color(0xFF334155)      // slate-700
)

/**
 * CompositionLocal for accessing [CryptoColors] throughout the app.
 *
 * Usage in Composable functions:
 * ```kotlin
 * val colors = LocalCryptoColors.current
 * Text(color = colors.textPrimary)
 * ```
 *
 * The value is provided by [CoinRoutineTheme] and defaults to [DarkCryptoColors].
 */
val LocalCryptoColors = compositionLocalOf { DarkCryptoColors }
