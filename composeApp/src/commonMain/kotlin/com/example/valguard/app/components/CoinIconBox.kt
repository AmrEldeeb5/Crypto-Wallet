/**
 * CoinIconBox.kt
 *
 * Reusable component for displaying coin icons with consistent styling across the app.
 * Provides a standardized container with gradient background for all coin icon displays.
 *
 * Features:
 * - Consistent gradient background using theme colors
 * - Configurable size and corner radius
 * - Centered icon display with proper scaling
 * - Accessibility support
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.valguard.theme.LocalCryptoColors

/**
 * Standardized coin icon container with gradient background.
 *
 * This component ensures visual consistency across all screens where coin icons are displayed.
 * Uses a subtle gradient from the provided border color for a premium, polished look.
 *
 * @param iconUrl URL of the coin icon to display
 * @param contentDescription Accessibility description for the icon
 * @param modifier Optional modifier for the container
 * @param size Size of the container box (default: 56dp)
 * @param iconSize Size of the icon inside the box (default: 32dp)
 * @param cornerRadius Corner radius of the container (default: 16dp)
 * @param borderColor Color used for the gradient background (default: accentPurple400)
 */
@Composable
fun CoinIconBox(
    iconUrl: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    iconSize: Dp = 32.dp,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = LocalCryptoColors.current.accentPurple400
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.2f),
                        borderColor.copy(alpha = 0.05f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = iconUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(iconSize)
        )
    }
}
