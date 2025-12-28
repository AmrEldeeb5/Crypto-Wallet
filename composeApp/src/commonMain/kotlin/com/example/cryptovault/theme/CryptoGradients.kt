/**
 * CryptoGradients.kt
 *
 * Provides gradient brush definitions for the CryptoVault application.
 * Gradients are used throughout the app for visual appeal and to create
 * a modern, polished look for buttons, backgrounds, and decorative elements.
 *
 * Available gradients:
 * - Brand gradients: Blue-to-purple and full brand spectrum
 * - Semantic gradients: Profit (green) and loss (red)
 * - Utility gradients: Subtle card overlays, coin icon backgrounds
 *
 * All gradient functions are @Composable to access theme colors dynamically,
 * ensuring proper adaptation to dark/light themes.
 *
 * @see CryptoColors for the base colors used in gradients
 */
package com.example.cryptovault.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Object containing all gradient definitions for CryptoVault.
 *
 * Provides factory functions for creating [Brush] instances that can be
 * used with Compose modifiers like `Modifier.background(brush)`.
 */
object CryptoGradients {
    
    /**
     * Creates a horizontal gradient from blue to purple.
     *
     * Commonly used for primary action buttons and highlighted elements.
     *
     * @return A horizontal [Brush] gradient from blue-500 to purple-500.
     */
    @Composable
    fun blueToPerple(): Brush {
        val colors = LocalCryptoColors.current
        return Brush.horizontalGradient(
            colors = listOf(
                colors.accentBlue500,
                colors.accentPurple500
            )
        )
    }
    
    /**
     * Creates the full brand gradient with blue, purple, and pink.
     *
     * Used for prominent branding elements, headers, and special highlights.
     *
     * @return A horizontal [Brush] gradient through the brand color spectrum.
     */
    @Composable
    fun brandGradient(): Brush {
        val colors = LocalCryptoColors.current
        return Brush.horizontalGradient(
            colors = listOf(
                colors.accentBlue400,
                colors.accentPurple400,
                colors.accentPink400
            )
        )
    }
    
    /**
     * Creates a vertical version of the brand gradient.
     *
     * Used for vertical elements like sidebars or tall cards.
     *
     * @return A vertical [Brush] gradient through the brand color spectrum.
     */
    @Composable
    fun brandGradientVertical(): Brush {
        val colors = LocalCryptoColors.current
        return Brush.verticalGradient(
            colors = listOf(
                colors.accentBlue400,
                colors.accentPurple400,
                colors.accentPink400
            )
        )
    }
    
    /**
     * Creates a subtle, semi-transparent card gradient overlay.
     *
     * Used for adding visual interest to cards without overwhelming content.
     *
     * @param alpha The opacity of the gradient colors (0.0 to 1.0). Default is 0.2.
     * @return A horizontal [Brush] gradient with reduced opacity.
     */
    @Composable
    fun subtleCardGradient(alpha: Float = 0.2f): Brush {
        val colors = LocalCryptoColors.current
        return Brush.horizontalGradient(
            colors = listOf(
                colors.accentBlue500.copy(alpha = alpha),
                colors.accentPurple500.copy(alpha = alpha),
                colors.accentPink500.copy(alpha = alpha)
            )
        )
    }
    
    /**
     * Creates a vertical gradient for profit/positive indicators.
     *
     * Fades from solid profit color to semi-transparent, useful for
     * chart backgrounds or positive change indicators.
     *
     * @return A vertical [Brush] gradient from profit color to transparent.
     */
    @Composable
    fun profitGradient(): Brush {
        val colors = LocalCryptoColors.current
        return Brush.verticalGradient(
            colors = listOf(
                colors.profit,
                colors.profit.copy(alpha = 0.3f)
            )
        )
    }
    
    /**
     * Creates a vertical gradient for loss/negative indicators.
     *
     * Fades from solid loss color to semi-transparent, useful for
     * chart backgrounds or negative change indicators.
     *
     * @return A vertical [Brush] gradient from loss color to transparent.
     */
    @Composable
    fun lossGradient(): Brush {
        val colors = LocalCryptoColors.current
        return Brush.verticalGradient(
            colors = listOf(
                colors.loss,
                colors.loss.copy(alpha = 0.3f)
            )
        )
    }
    
    /**
     * Generates a unique gradient for a cryptocurrency icon based on its symbol.
     *
     * Uses the symbol's hash code to deterministically generate consistent
     * colors for each coin, ensuring the same coin always gets the same gradient.
     *
     * @param symbol The cryptocurrency symbol (e.g., "BTC", "ETH").
     * @return A linear [Brush] gradient unique to the given symbol.
     */
    fun coinIconGradient(symbol: String): Brush {
        val hash = symbol.hashCode()
        val hue1 = (hash and 0xFF) / 255f * 360f
        val hue2 = ((hash shr 8) and 0xFF) / 255f * 360f
        
        return Brush.linearGradient(
            colors = listOf(
                Color.hsl(hue1, 0.7f, 0.5f),
                Color.hsl(hue2, 0.7f, 0.4f)
            )
        )
    }
}

/**
 * Extension function to create an active tab indicator gradient.
 *
 * Used for highlighting the currently selected tab in navigation.
 *
 * @return A horizontal [Brush] gradient from blue to purple.
 */
@Composable
fun CryptoColors.activeTabGradient(): Brush = Brush.horizontalGradient(
    colors = listOf(accentBlue500, accentPurple500)
)

/**
 * Extension function to create a title/header gradient.
 *
 * Used for gradient text effects on titles and headers.
 *
 * @return A horizontal [Brush] gradient through the full brand spectrum.
 */
@Composable
fun CryptoColors.titleGradient(): Brush = Brush.horizontalGradient(
    colors = listOf(accentBlue400, accentPurple400, accentPink400)
)
