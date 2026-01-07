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
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import com.example.valguard.app.coins.presentation.component.PerformanceChart
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.valguard.theme.LocalCryptoColors
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
    borderColor: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    showHoldings: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val dimensions = AppTheme.dimensions

    // Use sparkline color logic for neutral color support
    val changeColor = getSparklineColor(
        symbol = coin.symbol,
        changePercent = coin.changePercent,
        isPositive = coin.isPositive
    )

    // Build accessibility description
    val accessibilityDescription = buildString {
        append("${coin.name}, ${coin.symbol}. ")
        append("Price: ${coin.formattedPrice}. ")
        append("Change: ${coin.formattedChange}. ")
        if (coin.holdingsAmount != null) {
            append("Holding: ${coin.holdingsAmount}. ")
        }
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
            CoinIconBox(
                iconUrl = coin.iconUrl,
                contentDescription = null,
                size = 56.dp,
                iconSize = dimensions.coinIconSize,
                cornerRadius = 16.dp,
                borderColor = borderColor
            )

            Spacer(modifier = Modifier.width(dimensions.itemSpacing))

            // Coin name and symbol
            Column(
                modifier = Modifier.weight(0.25f)
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

            // Sparkline chart (if data available)
            if (coin.sparklineData.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(0.35f)
                        .height(40.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    PerformanceChart(
                        nodes = coin.sparklineData,
                        profitColor = changeColor,
                        lossColor = changeColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(0.35f))
            }

            // Price and change section
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(0.4f)
            ) {
                Text(
                    text = coin.formattedPrice,
                    style = typography.titleMedium,
                    color = colors.textPrimary
                )

                PriceChangeIndicator(
                    changePercent = coin.changePercent,
                    formattedChange = coin.formattedChange,
                    isPositive = coin.isPositive,
                    textStyle = typography.bodySmall,
                    iconSize = 16.dp,
                    color = changeColor
                )

                // Holdings indicator (only shown when holdings exist)
                if (coin.holdingsAmount != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Holding: ${coin.holdingsAmount}",
                        style = typography.caption,
                        color = colors.textTertiary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}