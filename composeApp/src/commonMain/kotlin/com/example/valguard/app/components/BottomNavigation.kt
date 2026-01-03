/**
 * BottomNavigation.kt
 *
 * Provides the main bottom navigation bar for the Valguard app.
 * Allows users to switch between major app sections.
 *
 * Navigation items:
 * - Market: Browse all cryptocurrencies
 * - Portfolio: View owned coins and performance
 * - DCA: Dollar-cost averaging calculator
 * - Compare: Compare multiple coins
 * - Alerts: Price alert management
 *
 * @see BottomNavItem for available navigation destinations
 */
package com.example.valguard.app.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.LocalCryptoColors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import valguard.composeapp.generated.resources.Res
import valguard.composeapp.generated.resources.material_symbols__bar_chart_4_bars_rounded
import valguard.composeapp.generated.resources.material_symbols__finance_mode_rounded
import valguard.composeapp.generated.resources.solar__repeat_linear
import valguard.composeapp.generated.resources.solar__square_sort_horizontal_linear
import valguard.composeapp.generated.resources.solar__wallet_money_outline

// Opacity constants for visual hierarchy
private const val BACKGROUND_ICON_ALPHA = 0.62f    // Icons as background scaffolding
private const val SECONDARY_LABEL_ALPHA = 0.78f    // Labels as cognitive anchors
private const val ACTIVE_ALPHA = 1.0f              // Full opacity for active state
private const val OPACITY_ANIMATION_DURATION = 150 // Smooth, premium-feeling transitions


/**
 * Enum defining the available bottom navigation destinations.
 *
 * Each item has a display label and associated icon.
 *
 * @property label Human-readable label shown below the icon
 * @property icon Material icon representing the destination
 */
enum class BottomNavItem(
    val label: String,
    val icon: DrawableResource
) {
    /** Market overview showing all cryptocurrencies */
    MARKET("Market", Res.drawable.material_symbols__finance_mode_rounded),
    /** User's portfolio with owned coins */
    PORTFOLIO("Portfolio", Res.drawable.solar__wallet_money_outline),
    /** Dollar-cost averaging calculator */
    DCA("DCA", Res.drawable.solar__repeat_linear),
    /** Coin comparison tool */
    COMPARE("Compare", Res.drawable.solar__square_sort_horizontal_linear)
}

/**
 * Main bottom navigation bar component.
 *
 * Displays all navigation items horizontally with the active item
 * highlighted using a gradient background.
 *
 * @param activeItem Currently selected navigation item
 * @param onItemSelected Callback when a navigation item is tapped
 * @param modifier Optional modifier for the navigation bar
 */
@Composable
fun CryptoBottomNavigation(
    activeItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.backgroundSecondary)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem.entries.forEach { item ->
            BottomNavItemView(
                item = item,
                isActive = item == activeItem,
                onClick = { onItemSelected(item) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual navigation item view.
 *
 * Displays an icon and label vertically, with visual feedback
 * for the active state (gradient background, highlighted colors).
 *
 * @param item The navigation item to display
 * @param isActive Whether this item is currently selected
 * @param onClick Callback when the item is tapped
 * @param modifier Optional modifier for the item
 */
@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(16.dp)
    
    // Animated opacity for icons (dimmer when inactive)
    val iconAlpha by animateFloatAsState(
        targetValue = if (isActive) ACTIVE_ALPHA else BACKGROUND_ICON_ALPHA,
        animationSpec = tween(
            durationMillis = OPACITY_ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        ),
        label = "iconAlpha"
    )
    
    // Animated opacity for labels (brighter than icons when inactive)
    val labelAlpha by animateFloatAsState(
        targetValue = if (isActive) ACTIVE_ALPHA else SECONDARY_LABEL_ALPHA,
        animationSpec = tween(
            durationMillis = OPACITY_ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        ),
        label = "labelAlpha"
    )
    
    val backgroundModifier = if (isActive) {
        Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    colors.accentBlue500,
                    colors.accentPurple500
                )
            ),
            shape = shape
        )
    } else {
        Modifier.background(
            color = colors.backgroundSecondary,
            shape = shape
        )
    }
    
    // Base colors without alpha (alpha applied via Modifier.alpha)
    val iconTint = if (isActive) {
        colors.textPrimary
    } else {
        colors.textSecondary
    }
    
    val textColor = if (isActive) {
        colors.textPrimary
    } else {
        colors.textSecondary
    }
    
    Column(
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)  // Defensive touch target guarantee
            .clip(shape)
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .semantics { 
                contentDescription = "Navigate to ${item.label}"
                selected = isActive  // Accessibility: announce selected state
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(item.icon),
            contentDescription = item.label,
            tint = iconTint,
            modifier = Modifier
                .size(22.dp)
                .alpha(iconAlpha)  // Apply animated alpha
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            modifier = Modifier.alpha(labelAlpha),  // Apply animated alpha (higher than icon)
            maxLines = 1
        )
    }
}
