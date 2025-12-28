/**
 * LoadingPlaceholder.kt
 *
 * Provides shimmer loading placeholders for content that is being loaded.
 * Creates a polished loading experience with animated gradient effects.
 *
 * Components:
 * - shimmerBrush(): Creates the animated shimmer gradient
 * - CoinCardPlaceholder: Placeholder matching CoinCard layout
 * - LoadingPlaceholderList: Multiple placeholders for lists
 * - LoadingPlaceholder: Single placeholder alias
 *
 * @see Skeleton for additional skeleton components
 */
package com.example.cryptovault.app.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.cryptovault.theme.LocalCryptoColors
import com.example.cryptovault.theme.LocalCryptoShapes
import com.example.cryptovault.theme.LocalCryptoSpacing

/**
 * Creates an animated shimmer brush for loading placeholders.
 *
 * The brush animates diagonally across the component, creating
 * a shimmering effect that indicates loading state.
 *
 * @return An animated [Brush] for use with background modifiers
 */
@Composable
fun shimmerBrush(): Brush {
    val colors = LocalCryptoColors.current
    val transition = rememberInfiniteTransition(label = "shimmer")
    
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    
    return Brush.linearGradient(
        colors = listOf(
            colors.shimmerBase,
            colors.shimmerHighlight,
            colors.shimmerBase
        ),
        start = Offset(translateAnim - 200f, translateAnim - 200f),
        end = Offset(translateAnim, translateAnim)
    )
}

/**
 * Placeholder component matching the CoinCard layout.
 *
 * Shows shimmer boxes in place of coin icon, name, symbol,
 * price, and change percentage.
 *
 * @param modifier Optional modifier for the placeholder
 */
@Composable
fun CoinCardPlaceholder(
    modifier: Modifier = Modifier
) {
    val spacing = LocalCryptoSpacing.current
    val shapes = LocalCryptoShapes.current
    val shimmer = shimmerBrush()
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.md)
    ) {
        // Coin icon placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(shimmer)
        )
        
        Spacer(modifier = Modifier.width(spacing.md))
        
        // Name and symbol placeholders
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(16.dp)
                    .clip(shapes.button)
                    .background(shimmer)
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(12.dp)
                    .clip(shapes.button)
                    .background(shimmer)
            )
        }
        
        Spacer(modifier = Modifier.width(spacing.md))
        
        // Price and change placeholders
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(16.dp)
                    .clip(shapes.button)
                    .background(shimmer)
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(12.dp)
                    .clip(shapes.button)
                    .background(shimmer)
            )
        }
    }
}

/**
 * Displays multiple coin card placeholders in a vertical list.
 *
 * Useful for showing loading state when fetching a list of coins.
 *
 * @param itemCount Number of placeholder items to display (default: 5)
 * @param modifier Optional modifier for the list
 */
@Composable
fun LoadingPlaceholderList(
    itemCount: Int = 5,
    modifier: Modifier = Modifier
) {
    val spacing = LocalCryptoSpacing.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
        modifier = modifier
    ) {
        repeat(itemCount) {
            CoinCardPlaceholder()
        }
    }
}

/**
 * Single loading placeholder component.
 *
 * Alias for [CoinCardPlaceholder] for simpler usage.
 *
 * @param modifier Optional modifier for the placeholder
 */
@Composable
fun LoadingPlaceholder(
    modifier: Modifier = Modifier
) {
    CoinCardPlaceholder(modifier = modifier)
}
