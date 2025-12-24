package com.example.cryptowallet.app.components

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
import com.example.cryptowallet.theme.LocalCryptoColors
import com.example.cryptowallet.theme.LocalCryptoSpacing
import com.example.cryptowallet.theme.LocalCryptoTypography

/**
 * A header component for displaying coin information in trade screens.
 * 
 * @param iconUrl URL for the coin's icon
 * @param name The coin's name
 * @param currentPrice The formatted current price (optional)
 * @param modifier Modifier for the component
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
