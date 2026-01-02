package com.example.valguard.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: Screens,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)

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
