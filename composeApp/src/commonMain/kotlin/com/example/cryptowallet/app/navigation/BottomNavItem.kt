package com.example.cryptowallet.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing a bottom navigation item.
 * 
 * @param route The navigation route for this item
 * @param icon The icon to display when not selected
 * @param selectedIcon The icon to display when selected
 * @param label The text label for the item
 */
data class BottomNavItem(
    val route: Screens,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)

/**
 * Predefined bottom navigation items for the app.
 */
object BottomNavItems {
    val Portfolio = BottomNavItem(
        route = Screens.Portfolio,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        label = "Portfolio"
    )
    
    val Discover = BottomNavItem(
        route = Screens.Coins,
        icon = Icons.Outlined.Search,
        selectedIcon = Icons.Filled.Search,
        label = "Discover"
    )
    
    val items = listOf(Portfolio, Discover)
}
