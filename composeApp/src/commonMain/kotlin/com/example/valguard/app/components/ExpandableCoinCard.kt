/**
 * ExpandableCoinCard.kt
 *
 * An expandable card component for displaying cryptocurrency details.
 * Expands to show additional actions and information when tapped.
 *
 * Features:
 * - Compact view with icon, name, price, and mini chart
 * - Expanded view with timeframe selector and action buttons
 * - Buy/Sell/Alert actions
 * - Watchlist toggle
 * - Holdings display for portfolio context
 * - Animated expand/collapse transitions
 *
 * @see CoinCard for a simpler, non-expandable variant
 * @see Timeframe for available chart timeframes
 */
package com.example.valguard.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.valguard.theme.CryptoGradients
import com.example.valguard.theme.LocalCryptoColors

/**
 * Enum defining available chart timeframes.
 *
 * @property label Display label for the timeframe button
 */
enum class Timeframe(val label: String) {
    /** 1 hour timeframe */
    H1("1H"),
    /** 24 hours timeframe */
    H24("24H"),
    /** 7 days timeframe */
    D7("7D"),
    /** 1 month timeframe */
    M1("1M"),
    /** 1 year timeframe */
    Y1("1Y")
}

/**
 * Expandable card component for cryptocurrency display.
 *
 * Shows basic coin info in collapsed state, and reveals additional
 * actions and details when expanded.
 *
 * @param coin The coin data to display
 * @param isExpanded Whether the card is currently expanded
 * @param selectedTimeframe Currently selected chart timeframe
 * @param onCardClick Callback when the card is tapped (toggle expand)
 * @param onTimeframeSelected Callback when a timeframe button is tapped
 * @param onSetAlertClick Callback for "Set Alert" button
 * @param onBuyClick Callback for "Buy" button
 * @param onSellClick Callback for "Sell" button
 * @param onWatchlistToggle Callback for watchlist star toggle
 * @param showHoldings Whether to display holdings section
 * @param modifier Optional modifier for the card
 */
@Composable
fun ExpandableCoinCard(
    coin: UiCoinItem,
    isExpanded: Boolean,
    selectedTimeframe: Timeframe,
    onCardClick: () -> Unit,
    onTimeframeSelected: (Timeframe) -> Unit,
    onSetAlertClick: () -> Unit,
    onBuyClick: () -> Unit,
    onSellClick: () -> Unit,
    onWatchlistToggle: () -> Unit,
    showHoldings: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(16.dp)
    val changeColor = if (coin.isPositive) colors.profit else colors.loss
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.cardBackground.copy(alpha = 0.4f))
            .border(1.dp, colors.cardBorder, shape)
            .clickable(onClick = onCardClick)
    ) {
        // Main content row (Header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin icon with rounded square shape
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CryptoGradients.coinIconGradient(coin.symbol)),
                contentAlignment = Alignment.Center
            ) {
                if (coin.iconUrl.isNotEmpty()) {
                    AsyncImage(
                        model = coin.iconUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        text = coin.symbol.take(2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Symbol and name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coin.symbol,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = coin.name,
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            }
            
            // Chart preview (Right aligned)
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(40.dp)
                    .padding(vertical = 4.dp)
            ) {
                ChartPreview(
                    isPositive = coin.isPositive,
                    data = coin.sparklineData,
                    seed = coin.id.hashCode()
                )
            }
        }
        
        // Stats Grid Row (Price, Change, Market Cap)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price Column
            Column {
                Text(
                    text = "Price",
                    fontSize = 12.sp,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = coin.formattedPrice,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
            
            // Change Column
            Column {
                Text(
                    text = "24h Change",
                    fontSize = 12.sp,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriceIndicator(direction = coin.priceDirection)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = coin.formattedChange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = changeColor
                    )
                }
            }
            
            // Market Cap Column
            Column {
                Text(
                    text = "Market Cap",
                    fontSize = 12.sp,
                    color = colors.textTertiary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = coin.marketCap ?: "-",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
            }
        }
        
        // Holdings section
        if (showHoldings && coin.hasHoldings()) {
            HorizontalDivider(
                color = colors.cardBorder,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Your Holdings Row (Amount)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Holdings",
                    fontSize = 14.sp,
                    color = colors.textTertiary
                )
                Text(
                    text = coin.holdingsAmount ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentBlue400
                )
            }
            
            // Value Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Value",
                    fontSize = 14.sp,
                    color = colors.textTertiary
                )
                Text(
                    text = coin.holdingsValue ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.profit
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Expanded section
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                HorizontalDivider(
                    color = colors.cardBorder,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                // Timeframe selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Timeframe.entries.forEach { timeframe ->
                        TimeframeButton(
                            timeframe = timeframe,
                            isSelected = timeframe == selectedTimeframe,
                            onClick = { onTimeframeSelected(timeframe) }
                        )
                    }
                }
                
                // Action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Watchlist toggle
                    IconButton(
                        onClick = onWatchlistToggle,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (coin.isInWatchlist) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = if (coin.isInWatchlist) "Remove from watchlist" else "Add to watchlist",
                            tint = if (coin.isInWatchlist) colors.accentPink400 else colors.textSecondary
                        )
                    }
                    
                    // Set Alert button
                    OutlinedButton(
                        onClick = onSetAlertClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.textPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.horizontalGradient(
                                colors = listOf(colors.accentBlue500, colors.accentPurple500)
                            )
                        )
                    ) {
                        Text("Set Alert", fontSize = 12.sp)
                    }
                    
                    // Buy button
                    Button(
                        onClick = onBuyClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.profit
                        )
                    ) {
                        Text("Buy", fontSize = 12.sp, color = colors.backgroundPrimary)
                    }
                    
                    // Sell button (only if has holdings)
                    if (coin.hasHoldings()) {
                        Button(
                            onClick = onSellClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.loss
                            )
                        ) {
                            Text("Sell", fontSize = 12.sp, color = colors.backgroundPrimary)
                        }
                    }
                }
                
                // Volume and supply stats
                if (coin.volume24h != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "24h Volume",
                                fontSize = 10.sp,
                                color = colors.textTertiary
                            )
                            Text(
                                text = coin.volume24h,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Timeframe selection button for the expanded card.
 *
 * Shows a pill-shaped button with gradient background when selected.
 *
 * @param timeframe The timeframe this button represents
 * @param isSelected Whether this timeframe is currently selected
 * @param onClick Callback when the button is tapped
 */
@Composable
private fun TimeframeButton(
    timeframe: Timeframe,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(8.dp)
    
    val backgroundModifier = if (isSelected) {
        Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    colors.accentBlue500.copy(alpha = 0.3f),
                    colors.accentPurple500.copy(alpha = 0.3f)
                )
            ),
            shape = shape
        )
    } else {
        Modifier.background(colors.cardBackgroundElevated, shape)
    }
    
    Box(
        modifier = Modifier
            .clip(shape)
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeframe.label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) colors.accentBlue400 else colors.textSecondary
        )
    }
}
