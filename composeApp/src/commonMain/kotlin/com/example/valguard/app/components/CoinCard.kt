/**
 * CoinCard.kt
 *
 * Displays a cryptocurrency in a card format with price and change information.
 * Used in lists throughout the app to show coin details at a glance.
 *
 * Features:
 * - Coin icon, name, and symbol display
 * - Current price with real-time direction indicator
 * - Percentage change with color coding
 * - Optional holdings display for portfolio views
 * - Long-press support for additional actions
 * - Full accessibility support
 *
 * @see UiCoinItem for the data model
 * @see ExpandableCoinCard for an expandable variant
 */
package com.example.valguard.app.components

import androidx.compose.foundation.ExperimentalFoundationApi
import com.example.valguard.app.realtime.domain.PriceDirection
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.valguard.theme.LocalCryptoShapes
import com.example.valguard.theme.LocalCryptoSpacing
import com.example.valguard.theme.LocalCryptoTypography
import com.example.valguard.theme.AppTheme

/** Minimum touch target size for accessibility compliance (48dp) */
val MinTouchTargetSize = 48.dp

/**
 * Card component displaying cryptocurrency information.
 *
 * Shows coin icon, name, symbol, current price, and percentage change.
 * Optionally displays user's holdings when in portfolio context.
 *
 * @param coin The coin data to display
 * @param onClick Callback when the card is tapped
 * @param onLongClick Optional callback for long-press action
 * @param showHoldings Whether to display holdings information
 * @param modifier Optional modifier for the card
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoinCard(
    coin: UiCoinItem,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    showHoldings: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val spacing = LocalCryptoSpacing.current
    val shapes = LocalCryptoShapes.current
    val dimensions = AppTheme.dimensions

    val changeColor = if (coin.isPositive) colors.profit else colors.loss

    // Build accessibility description
    val accessibilityDescription = buildString {
        append("${coin.name}, ${coin.symbol}. ")
        append("Price: ${coin.formattedPrice}. ")
        append("Change: ${coin.formattedChange}. ")
        if (showHoldings && coin.hasHoldings()) {
            append("Holdings: ${coin.holdingsAmount}, worth ${coin.holdingsValue}.")
        }
    }

    Card(
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = colors.cardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.cardElevation),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = MinTouchTargetSize)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .semantics {
                contentDescription = accessibilityDescription
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding)
        ) {
            // Coin icon
            Box(
                modifier = Modifier
                    .size(dimensions.coinIconSize)
                    .clip(CircleShape)
                    .background(colors.cardBackgroundElevated)
            ) {
                AsyncImage(
                    model = coin.iconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(dimensions.coinIconSize)
                )
            }

            Spacer(modifier = Modifier.width(dimensions.itemSpacing))

            // Coin name and symbol
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coin.name,
                    style = typography.titleMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = coin.symbol,
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
            }

            // Price and change section
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = coin.formattedPrice,
                        style = typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(dimensions.smallSpacing))
                    PriceIndicator(direction = coin.priceDirection)
                }

                Text(
                    text = coin.formattedChange,
                    style = typography.bodyMedium,
                    color = changeColor
                )

                // Holdings section (only shown when showHoldings is true and holdings exist)
                if (showHoldings && coin.hasHoldings()) {
                    Spacer(modifier = Modifier.height(dimensions.smallSpacing))
                    Text(
                        text = coin.holdingsAmount ?: "",
                        style = typography.caption,
                        color = colors.textSecondary
                    )
                    Text(
                        text = coin.holdingsValue ?: "",
                        style = typography.caption,
                        color = colors.textTertiary
                    )
                }
            }
        }
    }
}