/**
 * Skeleton.kt
 *
 * Skeleton loading components for various UI elements.
 * Provides shimmer-animated placeholders while content loads.
 *
 * Components:
 * - SkeletonBox: Generic rectangular skeleton
 * - SkeletonText: Text-sized skeleton
 * - SkeletonCircle: Circular skeleton (for avatars/icons)
 * - SkeletonCard: Full card skeleton matching CoinCard layout
 * - SkeletonCoinCard: Enhanced coin card skeleton with accessibility
 * - SkeletonCoinList: List of skeleton coin cards with performance cap
 * - SkeletonCoinDetailHeader: Header skeleton for coin detail screen
 *
 * @see LoadingPlaceholder for additional loading components
 * @see shimmerBrush for the animation brush
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.valguard.theme.AppTheme
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoSpacing

/**
 * Generic skeleton box with shimmer animation.
 *
 * @param modifier Modifier for sizing and positioning
 * @param shape Shape of the skeleton (default: 8dp rounded corners)
 */
@Composable
fun SkeletonBox(modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(8.dp)) {
    Box(modifier = modifier.clip(shape).background(shimmerBrush()))
}

/**
 * Text-sized skeleton placeholder.
 *
 * @param width Width of the skeleton
 * @param modifier Optional modifier
 * @param height Height of the skeleton (default: 16dp)
 */
@Composable
fun SkeletonText(width: Dp, modifier: Modifier = Modifier, height: Dp = 16.dp) {
    SkeletonBox(modifier = modifier.width(width).height(height), shape = RoundedCornerShape(4.dp))
}

/**
 * Circular skeleton for avatars and icons.
 *
 * @param size Diameter of the circle
 * @param modifier Optional modifier
 */
@Composable
fun SkeletonCircle(size: Dp, modifier: Modifier = Modifier) {
    SkeletonBox(modifier = modifier.size(size), shape = CircleShape)
}

/**
 * Full card skeleton matching CoinCard layout.
 *
 * Shows placeholders for icon, name, symbol, price, and change.
 * Includes accessibility support.
 *
 * @param modifier Optional modifier for the card
 */
@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val colors = LocalCryptoColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .padding(16.dp)
            .semantics { contentDescription = "Loading content" }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                SkeletonText(width = 100.dp, height = 18.dp)
                SkeletonText(width = 60.dp, height = 14.dp)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonText(width = 80.dp, height = 18.dp)
                SkeletonText(width = 50.dp, height = 14.dp)
            }
        }
    }
}

/**
 * Header skeleton for coin detail screen.
 *
 * Shows placeholders for coin icon, name, symbol, and action button.
 * Includes accessibility support.
 *
 * @param modifier Optional modifier for the header
 */
@Composable
fun SkeletonCoinDetailHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Loading coin header" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SkeletonBox(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonText(width = 120.dp, height = 28.dp)
                SkeletonText(width = 60.dp, height = 16.dp)
            }
        }
        SkeletonBox(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp))
    }
}


/**
 * Enhanced coin card skeleton matching CoinCard layout exactly.
 * 
 * Includes:
 * - Coin icon (circle)
 * - Name/Symbol placeholders
 * - Sparkline placeholder
 * - Price/Change placeholders
 * - Accessibility support with contentDescription
 * - Non-interactive (blocks pointer events)
 *
 * @param modifier Optional modifier for the card
 */
@Composable
fun SkeletonCoinCard(modifier: Modifier = Modifier) {
    val colors = LocalCryptoColors.current
    val dimensions = AppTheme.dimensions
    
    Card(
        shape = RoundedCornerShape(dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = MinTouchTargetSize)
            .semantics { 
                contentDescription = "Loading coin information"
            }
            .pointerInput(Unit) {
                // Consume all pointer events to prevent interaction during loading
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.cardPadding)
        ) {
            // Icon placeholder (circle) - matches CoinCard icon
            SkeletonCircle(size = dimensions.coinIconSize)
            
            Spacer(modifier = Modifier.width(dimensions.itemSpacing))
            
            // Name/Symbol placeholders - matches CoinCard text column
            Column(modifier = Modifier.weight(0.25f)) {
                SkeletonText(width = 48.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 64.dp, height = 12.dp)
            }
            
            // Sparkline placeholder - matches CoinCard sparkline area
            SkeletonBox(
                modifier = Modifier
                    .weight(0.35f)
                    .height(40.dp)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(4.dp)
            )
            
            // Price/Change placeholders - matches CoinCard price column
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(0.4f)
            ) {
                SkeletonText(width = 72.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 48.dp, height = 12.dp)
            }
        }
    }
}

/**
 * List of skeleton coin cards for loading states.
 * 
 * Performance-capped at 8 items maximum to prevent shimmer animation issues.
 * Default is 6 items for optimal visual balance.
 *
 * @param itemCount Number of skeleton cards to display (default: 6, max: 8)
 * @param modifier Optional modifier for the list
 */
@Composable
fun SkeletonCoinList(
    itemCount: Int = 6,
    modifier: Modifier = Modifier
) {
    val spacing = LocalCryptoSpacing.current
    val safeCount = itemCount.coerceAtMost(8) // Performance cap
    
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
        modifier = modifier.semantics {
            contentDescription = "Loading $safeCount coins"
        }
    ) {
        repeat(safeCount) {
            SkeletonCoinCard(
                modifier = Modifier.padding(vertical = spacing.xs)
            )
        }
    }
}
