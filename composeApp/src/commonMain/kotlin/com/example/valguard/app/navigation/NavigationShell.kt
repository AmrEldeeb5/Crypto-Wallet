package com.example.valguard.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.valguard.theme.LocalCryptoColors
import com.example.valguard.theme.LocalCryptoTypography

@Composable
fun NavigationShell(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    val colors = LocalCryptoColors.current
    val typography = LocalCryptoTypography.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Determine if we should show the bottom bar (hide for modal screens like Buy/Sell)
    val showBottomBar = currentDestination?.let { dest ->
        BottomNavItems.items.any { item ->
            dest.hasRoute(item.route::class)
        }
    } ?: true
    
    Scaffold(
        containerColor = colors.cardBackgroundElevated,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = colors.cardBackground
                ) {
                    BottomNavItems.items.forEach { item ->
                        val selected = currentDestination?.hasRoute(item.route::class) == true
                        
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        // Pop up to the start destination to avoid building up a large stack
                                        popUpTo(Screens.Portfolio) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = typography.labelMedium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colors.buttonPrimary,
                                selectedTextColor = colors.buttonPrimary,
                                unselectedIconColor = colors.textSecondary,
                                unselectedTextColor = colors.textSecondary,
                                indicatorColor = colors.buttonSecondary
                            ),
                            modifier = Modifier.semantics {
                                contentDescription = "${item.label} tab${if (selected) ", selected" else ""}"
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        content(Modifier.padding(paddingValues))
    }
}
