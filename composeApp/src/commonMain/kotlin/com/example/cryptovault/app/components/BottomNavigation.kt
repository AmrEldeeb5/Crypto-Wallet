/**
 * BottomNavigation.kt
 *
 * Provides the main bottom navigation bar for the CryptoVault app.
 * Allows users to switch between major app sections.
 *
 * Navigation items:
 * - Market: Browse all cryptocurrencies
 * - Portfolio: View owned coins and performance
 * - DCA: Dollar-cost averaging calculator
 * - Compare: Compare multiple coins
 * - Leaderboard: Top performing coins ranking
 * - Alerts: Price alert management
 *
 * @see BottomNavItem for available navigation destinations
 */
package com.example.cryptovault.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptovault.theme.LocalCryptoColors

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
    val icon: ImageVector
) {
    /** Market overview showing all cryptocurrencies */
    MARKET("Market", Icons.Default.Home),
    /** User's portfolio with owned coins */
    PORTFOLIO("Portfolio", Icons.Default.Star),
    /** Dollar-cost averaging calculator */
    DCA("DCA", Icons.Default.Refresh),
    /** Coin comparison tool */
    COMPARE("Compare", Icons.Default.List),
    /** Top coins leaderboard */
    LEADERBOARD("Rank", Icons.Default.ThumbUp),
    /** Price alerts management */
    ALERTS("Alerts", Icons.Default.Notifications)
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
            .background(colors.cardBackground.copy(alpha = 0.95f))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
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
    val shape = RoundedCornerShape(12.dp)
    
    val backgroundModifier = if (isActive) {
        Modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    colors.accentBlue500.copy(alpha = 0.2f),
                    colors.accentPurple500.copy(alpha = 0.2f)
                )
            ),
            shape = shape
        )
    } else {
        Modifier.background(Color.Transparent, shape)
    }
    
    val iconTint = if (isActive) {
        colors.accentBlue400
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
            .clip(shape)
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .semantics { contentDescription = "Navigate to ${item.label}" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor,
            maxLines = 1
        )
    }
}
