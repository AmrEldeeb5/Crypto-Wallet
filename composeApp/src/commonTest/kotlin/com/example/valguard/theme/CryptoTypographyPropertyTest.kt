package com.example.valguard.theme

import androidx.compose.ui.unit.TextUnit
import kotlin.test.Test
import kotlin.test.assertTrue

class CryptoTypographyPropertyTest {

    @Test
    fun `Property 2 - All typography styles should have valid font sizes`() {
        val typography = DefaultCryptoTypography
        
        // Display styles
        assertTrue(typography.displayLarge.fontSize.value > 0, "displayLarge fontSize should be positive")
        assertTrue(typography.displayMedium.fontSize.value > 0, "displayMedium fontSize should be positive")
        
        // Title styles
        assertTrue(typography.titleLarge.fontSize.value > 0, "titleLarge fontSize should be positive")
        assertTrue(typography.titleMedium.fontSize.value > 0, "titleMedium fontSize should be positive")
        assertTrue(typography.titleSmall.fontSize.value > 0, "titleSmall fontSize should be positive")
        
        // Body styles
        assertTrue(typography.bodyLarge.fontSize.value > 0, "bodyLarge fontSize should be positive")
        assertTrue(typography.bodyMedium.fontSize.value > 0, "bodyMedium fontSize should be positive")
        assertTrue(typography.bodySmall.fontSize.value > 0, "bodySmall fontSize should be positive")
        
        // Label styles
        assertTrue(typography.labelLarge.fontSize.value > 0, "labelLarge fontSize should be positive")
        assertTrue(typography.labelMedium.fontSize.value > 0, "labelMedium fontSize should be positive")
        assertTrue(typography.labelSmall.fontSize.value > 0, "labelSmall fontSize should be positive")
        
        // Caption
        assertTrue(typography.caption.fontSize.value > 0, "caption fontSize should be positive")
    }

    @Test
    fun `Property 21 - All typography styles should use sp units for accessibility`() {
        val typography = DefaultCryptoTypography
        
        // All font sizes should be in sp (scalable pixels)
        // In Compose, TextUnit created with .sp is scalable
        assertTrue(typography.displayLarge.fontSize.isSp, "displayLarge should use sp units")
        assertTrue(typography.displayMedium.fontSize.isSp, "displayMedium should use sp units")
        assertTrue(typography.titleLarge.fontSize.isSp, "titleLarge should use sp units")
        assertTrue(typography.titleMedium.fontSize.isSp, "titleMedium should use sp units")
        assertTrue(typography.titleSmall.fontSize.isSp, "titleSmall should use sp units")
        assertTrue(typography.bodyLarge.fontSize.isSp, "bodyLarge should use sp units")
        assertTrue(typography.bodyMedium.fontSize.isSp, "bodyMedium should use sp units")
        assertTrue(typography.bodySmall.fontSize.isSp, "bodySmall should use sp units")
        assertTrue(typography.labelLarge.fontSize.isSp, "labelLarge should use sp units")
        assertTrue(typography.labelMedium.fontSize.isSp, "labelMedium should use sp units")
        assertTrue(typography.labelSmall.fontSize.isSp, "labelSmall should use sp units")
        assertTrue(typography.caption.fontSize.isSp, "caption should use sp units")
    }

    @Test
    fun `Property 2 - Typography hierarchy should be maintained`() {
        val typography = DefaultCryptoTypography
        
        // Display > Title > Body > Label > Caption (general hierarchy)
        assertTrue(
            typography.displayLarge.fontSize.value > typography.titleLarge.fontSize.value,
            "displayLarge should be larger than titleLarge"
        )
        assertTrue(
            typography.titleLarge.fontSize.value > typography.bodyLarge.fontSize.value,
            "titleLarge should be larger than bodyLarge"
        )
        assertTrue(
            typography.bodyLarge.fontSize.value >= typography.labelLarge.fontSize.value,
            "bodyLarge should be >= labelLarge"
        )
        assertTrue(
            typography.labelSmall.fontSize.value >= typography.caption.fontSize.value,
            "labelSmall should be >= caption"
        )
    }
}
