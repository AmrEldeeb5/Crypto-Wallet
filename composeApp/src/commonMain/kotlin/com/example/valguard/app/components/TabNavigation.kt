/**
 * TabNavigation.kt
 *
 * Horizontal tab navigation component for switching between views.
 * Used to toggle between Market, Portfolio, and Watchlist views.
 *
 * Features:
 * - Three tabs: Market, Portfolio, Watchlist
 * - Gradient background for active tab
 * - Smooth visual transitions
 *
 * @see Tab for available tab options
 * @see BottomNavigation for the main navigation bar
 */
package com.example.valguard.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.valguard.theme.LocalCryptoColors

/**
 * Enum defining available tabs in the tab navigation.
 *
 * @property label Display label for the tab
 */
enum class Tab(val label: String) {
    /** Market view showing all cryptocurrencies */
    MARKET("Market"),
    /** Portfolio view showing owned coins */
    PORTFOLIO("Portfolio"),
    /** Watchlist view showing favorited coins */
    WATCHLIST("Watchlist")
}

/**
 * Horizontal tab navigation component.
 *
 * Displays tabs in a row with the active tab highlighted
 * using a gradient background.
 *
 * @param activeTab Currently selected tab
 * @param onTabSelected Callback when a tab is tapped
 * @param modifier Optional modifier for the navigation
 */
@Composable
fun TabNavigation(
    activeTab: Tab,
    onTabSelected: (Tab) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(colors.cardBackground, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Tab.entries.forEach { tab ->
            TabItem(
                tab = tab,
                isActive = tab == activeTab,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual tab item in the navigation.
 *
 * Shows the tab label with gradient background when active.
 *
 * @param tab The tab to display
 * @param isActive Whether this tab is currently selected
 * @param onClick Callback when the tab is tapped
 * @param modifier Optional modifier for the tab
 */
@Composable
private fun TabItem(
    tab: Tab,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalCryptoColors.current
    val shape = RoundedCornerShape(8.dp)
    
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
            color = colors.cardBackground.copy(alpha = 0.3f),
            shape = shape
        )
    }
    
    Box(
        modifier = modifier
            .clip(shape)
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tab.label,
            fontSize = 14.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isActive) colors.textPrimary else colors.textSecondary.copy(alpha = 0.7f)
        )
    }
}
