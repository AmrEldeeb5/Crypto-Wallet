package com.example.valguard.app.navigation

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NavigationPropertyTest {

    @Test
    fun `Property 4 - Bottom nav items have unique routes`() {
        val items = BottomNavItems.items
        val routes = items.map { it.route }
        
        // All routes should be unique
        assertEquals(routes.size, routes.toSet().size, "All bottom nav items should have unique routes")
    }

    @Test
    fun `Property 4 - Bottom nav has exactly two items`() {
        val items = BottomNavItems.items
        
        assertEquals(2, items.size, "Bottom navigation should have exactly 2 items (Portfolio and Discover)")
    }

    @Test
    fun `Property 4 - Portfolio is the first nav item`() {
        val firstItem = BottomNavItems.items.first()
        
        assertEquals(Screens.Portfolio, firstItem.route, "Portfolio should be the first navigation item")
        assertEquals("Portfolio", firstItem.label, "First item should be labeled 'Portfolio'")
    }

    @Test
    fun `Property 4 - Discover is the second nav item`() {
        val secondItem = BottomNavItems.items[1]
        
        assertEquals(Screens.Coins, secondItem.route, "Discover should route to Coins screen")
        assertEquals("Discover", secondItem.label, "Second item should be labeled 'Discover'")
    }

    @Test
    fun `Property 4 - Each nav item has distinct selected and unselected icons`() {
        BottomNavItems.items.forEach { item ->
            assertNotEquals(
                item.icon,
                item.selectedIcon,
                "Nav item '${item.label}' should have different icons for selected and unselected states"
            )
        }
    }

    @Test
    fun `Property 4 - All nav items have non-empty labels`() {
        BottomNavItems.items.forEach { item ->
            assertTrue(
                item.label.isNotBlank(),
                "Nav item should have a non-empty label"
            )
        }
    }

    @Test
    fun `Property 4 - Navigation items are accessible`() {
        // Each nav item should have a label that can be used for accessibility
        BottomNavItems.items.forEach { item ->
            assertTrue(
                item.label.isNotBlank(),
                "Nav item '${item.route}' should have a label for accessibility"
            )
        }
    }
}
