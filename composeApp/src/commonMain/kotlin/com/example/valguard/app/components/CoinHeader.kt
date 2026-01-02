/**
 * CoinHeader.kt
 *
 * Displays a compact coin identifier with icon, name, and optional price.
 * Used in headers and trading screens to show which coin is being viewed.
 *
 * Features:
 * - Circular coin icon
 * - Coin name display
 * - Optional current price
 * - Pill-shaped border styling
 * - Accessibility support
 *
 * @see CoinCard for a full card display
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.LocalCryptoTypography

/**
 * Compact header showing coin icon, name, and optional price.
 *
 * Displays in a pill-shaped container, suitable for use in
 * trading screens and detail headers.
 *
 * @param iconUrl URL of the coin's icon image
 * @param name Display name of the cryptocurrency
 * @param currentPrice Optional formatted price string
 * @param modifier Optional modifier for the component
 */
@Composable
fun CoinHeader(
    iconUrl: String,
    name: String,
    currentPrice: String? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 1.dp,
                color = colors.border,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = spacing.md, vertical = spacing.xs)
            .semantics {
                contentDescription = "Trading $name${currentPrice?.let { " at $it" } ?: ""}"
            }
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colors.cardBackgroundElevated)
        ) {
            AsyncImage(
                model = iconUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(spacing.md))
        
        Text(
            text = name,
            style = typography.labelLarge,
            color = colors.textPrimary
        )
        
        if (currentPrice != null) {
            Spacer(modifier = Modifier.width(spacing.sm))
            Text(
                text = currentPrice,
                style = typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}
