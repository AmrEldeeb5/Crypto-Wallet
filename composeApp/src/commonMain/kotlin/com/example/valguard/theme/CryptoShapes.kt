/**
 * CryptoShapes.kt
 *
 * Defines the shape system for the Valguard application.
 * Provides consistent corner radius values for different UI components,
 * ensuring visual harmony across the app.
 *
 * Shape categories:
 * - Card: Standard card containers (12dp corners)
 * - Button: Interactive buttons (8dp corners)
 * - Chip: Tags and filter chips (16dp, fully rounded)
 * - BottomSheet: Modal bottom sheets (24dp top corners)
 * - Dialog: Alert and confirmation dialogs (16dp corners)
 * - InputField: Text fields and inputs (8dp corners)
 *
 * @see DefaultCryptoShapes for the default shape configuration
 * @see LocalCryptoShapes for accessing shapes via CompositionLocal
 */
package com.example.valguard.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Data class containing all shape definitions for the Valguard design system.
 *
 * This immutable class provides consistent corner radius values for UI components.
 * Use [LocalCryptoShapes] to access these shapes in Composable functions.
 *
 * @property card Shape for card containers (12dp rounded corners)
 * @property button Shape for buttons (8dp rounded corners)
 * @property chip Shape for chips and tags (16dp, pill-shaped)
 * @property bottomSheet Shape for bottom sheets (24dp top corners only)
 * @property dialog Shape for dialog windows (16dp rounded corners)
 * @property inputField Shape for text input fields (8dp rounded corners)
 */
@Immutable
data class CryptoShapes(
    val card: Shape = RoundedCornerShape(12.dp),
    val button: Shape = RoundedCornerShape(8.dp),
    val chip: Shape = RoundedCornerShape(16.dp),
    val bottomSheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val dialog: Shape = RoundedCornerShape(16.dp),
    val inputField: Shape = RoundedCornerShape(8.dp)
)

/**
 * Default shape configuration for Valguard.
 *
 * Uses standard rounded corner values that provide a modern,
 * friendly appearance while maintaining usability.
 */
val DefaultCryptoShapes = CryptoShapes()

/**
 * CompositionLocal for accessing [CryptoShapes] throughout the app.
 *
 * Usage in Composable functions:
 * ```kotlin
 * val shapes = LocalCryptoShapes.current
 * Card(shape = shapes.card) { ... }
 * ```
 *
 * The value is provided by [CoinRoutineTheme] and defaults to [DefaultCryptoShapes].
 */
val LocalCryptoShapes = compositionLocalOf { DefaultCryptoShapes }
