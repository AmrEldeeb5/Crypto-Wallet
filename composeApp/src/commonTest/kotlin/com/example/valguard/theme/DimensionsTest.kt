/**
 * DimensionsTest.kt
 *
 * Unit tests for the responsive dimension system.
 * Tests dimension factory functions, boundary conditions, and value hierarchies.
 */
package com.example.valguard.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DimensionsTest {
    
    @Test
    fun `small phone dimensions have correct values`() {
        val dimensions = createSmallPhoneDimensions()
        
        // Spacing
        assertEquals(16.dp, dimensions.screenPadding)
        assertEquals(12.dp, dimensions.cardPadding)
        assertEquals(8.dp, dimensions.itemSpacing)
        assertEquals(12.dp, dimensions.verticalSpacing)
        assertEquals(4.dp, dimensions.smallSpacing)
        
        // Icon sizes
        assertEquals(36.dp, dimensions.coinIconSize)
        assertEquals(48.dp, dimensions.appIconSize)
        
        // Component sizes
        assertEquals(48.dp, dimensions.buttonHeight)
        assertEquals(12.dp, dimensions.cardCornerRadius)
        assertEquals(3.dp, dimensions.cardElevation)
        
        // Grid
        assertEquals(130.dp, dimensions.coinCardMinWidth)
        assertEquals(2, dimensions.gridColumns)
    }
    
    @Test
    fun `medium phone dimensions have correct values`() {
        val dimensions = createMediumPhoneDimensions()
        
        // Spacing
        assertEquals(16.dp, dimensions.screenPadding)
        assertEquals(12.dp, dimensions.cardPadding)
        assertEquals(12.dp, dimensions.itemSpacing)
        assertEquals(16.dp, dimensions.verticalSpacing)
        assertEquals(8.dp, dimensions.smallSpacing)
        
        // Icon sizes
        assertEquals(44.dp, dimensions.coinIconSize)
        assertEquals(64.dp, dimensions.appIconSize)
        
        // Component sizes
        assertEquals(52.dp, dimensions.buttonHeight)
        assertEquals(16.dp, dimensions.cardCornerRadius)
        assertEquals(4.dp, dimensions.cardElevation)
        
        // Grid
        assertEquals(150.dp, dimensions.coinCardMinWidth)
        assertEquals(2, dimensions.gridColumns)
    }
    
    @Test
    fun `large phone dimensions have correct values`() {
        val dimensions = createLargePhoneDimensions()
        
        // Spacing
        assertEquals(20.dp, dimensions.screenPadding)
        assertEquals(16.dp, dimensions.cardPadding)
        assertEquals(14.dp, dimensions.itemSpacing)
        assertEquals(16.dp, dimensions.verticalSpacing)
        assertEquals(8.dp, dimensions.smallSpacing)
        
        // Icon sizes
        assertEquals(44.dp, dimensions.coinIconSize)
        assertEquals(64.dp, dimensions.appIconSize)
        
        // Component sizes
        assertEquals(52.dp, dimensions.buttonHeight)
        assertEquals(16.dp, dimensions.cardCornerRadius)
        assertEquals(4.dp, dimensions.cardElevation)
        
        // Grid
        assertEquals(150.dp, dimensions.coinCardMinWidth)
        assertEquals(2, dimensions.gridColumns)
    }
    
    @Test
    fun `tablet dimensions have correct values`() {
        val dimensions = createTabletDimensions()
        
        // Spacing
        assertEquals(32.dp, dimensions.screenPadding)
        assertEquals(20.dp, dimensions.cardPadding)
        assertEquals(18.dp, dimensions.itemSpacing)
        assertEquals(28.dp, dimensions.verticalSpacing)
        assertEquals(12.dp, dimensions.smallSpacing)
        
        // Icon sizes
        assertEquals(72.dp, dimensions.coinIconSize)
        assertEquals(96.dp, dimensions.appIconSize)
        
        // Component sizes
        assertEquals(60.dp, dimensions.buttonHeight)
        assertEquals(18.dp, dimensions.cardCornerRadius)
        assertEquals(6.dp, dimensions.cardElevation)
        
        // Grid
        assertEquals(180.dp, dimensions.coinCardMinWidth)
        assertEquals(3, dimensions.gridColumns)
    }
    
    @Test
    fun `spacing hierarchy is maintained in small phone dimensions`() {
        val dimensions = createSmallPhoneDimensions()
        
        assertTrue(dimensions.smallSpacing < dimensions.itemSpacing)
        assertTrue(dimensions.itemSpacing <= dimensions.verticalSpacing)
        assertTrue(dimensions.verticalSpacing <= dimensions.cardPadding)
        assertTrue(dimensions.cardPadding < dimensions.screenPadding)
    }
    
    @Test
    fun `spacing hierarchy is maintained in medium phone dimensions`() {
        val dimensions = createMediumPhoneDimensions()
        
        assertTrue(dimensions.smallSpacing < dimensions.itemSpacing)
        assertTrue(dimensions.itemSpacing <= dimensions.verticalSpacing)
        assertTrue(dimensions.verticalSpacing <= dimensions.cardPadding)
        assertTrue(dimensions.cardPadding < dimensions.screenPadding)
    }
    
    @Test
    fun `spacing hierarchy is maintained in large phone dimensions`() {
        val dimensions = createLargePhoneDimensions()
        
        assertTrue(dimensions.smallSpacing < dimensions.itemSpacing)
        assertTrue(dimensions.itemSpacing <= dimensions.verticalSpacing)
        assertTrue(dimensions.verticalSpacing <= dimensions.cardPadding)
        assertTrue(dimensions.cardPadding < dimensions.screenPadding)
    }
    
    @Test
    fun `spacing hierarchy is maintained in tablet dimensions`() {
        val dimensions = createTabletDimensions()
        
        assertTrue(dimensions.smallSpacing < dimensions.itemSpacing)
        assertTrue(dimensions.itemSpacing <= dimensions.verticalSpacing)
        assertTrue(dimensions.verticalSpacing <= dimensions.cardPadding)
        assertTrue(dimensions.cardPadding < dimensions.screenPadding)
    }
    
    @Test
    fun `button height meets minimum touch target size for all screen sizes`() {
        val smallPhone = createSmallPhoneDimensions()
        val mediumPhone = createMediumPhoneDimensions()
        val largePhone = createLargePhoneDimensions()
        val tablet = createTabletDimensions()
        
        // Minimum 46dp for accessibility
        assertTrue(smallPhone.buttonHeight.value >= 46f, "Small phone button height should be >= 46dp")
        assertTrue(mediumPhone.buttonHeight.value >= 46f, "Medium phone button height should be >= 46dp")
        assertTrue(largePhone.buttonHeight.value >= 46f, "Large phone button height should be >= 46dp")
        assertTrue(tablet.buttonHeight.value >= 46f, "Tablet button height should be >= 46dp")
    }
    
    @Test
    fun `coin icon with padding meets minimum touch target size`() {
        val smallPhone = createSmallPhoneDimensions()
        val mediumPhone = createMediumPhoneDimensions()
        val largePhone = createLargePhoneDimensions()
        val tablet = createTabletDimensions()
        
        // Icon + padding should be >= 48dp
        assertTrue(
            (smallPhone.coinIconSize.value + smallPhone.cardPadding.value * 2) >= 48f,
            "Small phone coin icon with padding should be >= 48dp"
        )
        assertTrue(
            (mediumPhone.coinIconSize.value + mediumPhone.cardPadding.value * 2) >= 48f,
            "Medium phone coin icon with padding should be >= 48dp"
        )
        assertTrue(
            (largePhone.coinIconSize.value + largePhone.cardPadding.value * 2) >= 48f,
            "Large phone coin icon with padding should be >= 48dp"
        )
        assertTrue(
            (tablet.coinIconSize.value + tablet.cardPadding.value * 2) >= 48f,
            "Tablet coin icon with padding should be >= 48dp"
        )
    }
    
    @Test
    fun `icon sizes scale proportionally between screen sizes`() {
        val smallPhone = createSmallPhoneDimensions()
        val mediumPhone = createMediumPhoneDimensions()
        val largePhone = createLargePhoneDimensions()
        val tablet = createTabletDimensions()
        
        // Each larger size should be at least 1.0x the previous (allowing for same size)
        val smallToMediumRatio = mediumPhone.coinIconSize.value / smallPhone.coinIconSize.value
        val mediumToLargeRatio = largePhone.coinIconSize.value / mediumPhone.coinIconSize.value
        val largeToTabletRatio = tablet.coinIconSize.value / largePhone.coinIconSize.value
        
        assertTrue(smallToMediumRatio >= 1.0f, "Medium icons should be >= small icons")
        assertTrue(mediumToLargeRatio >= 1.0f, "Large icons should be >= medium icons")
        assertTrue(largeToTabletRatio >= 1.0f, "Tablet icons should be >= large icons")
    }
    
    @Test
    fun `component sizes scale proportionally between screen sizes`() {
        val smallPhone = createSmallPhoneDimensions()
        val mediumPhone = createMediumPhoneDimensions()
        val largePhone = createLargePhoneDimensions()
        val tablet = createTabletDimensions()
        
        // Each larger size should be at least 1.1x the previous
        val smallToMediumRatio = mediumPhone.coinIconSize.value / smallPhone.coinIconSize.value
        val mediumToLargeRatio = largePhone.coinIconSize.value / mediumPhone.coinIconSize.value
        val largeToTabletRatio = tablet.coinIconSize.value / largePhone.coinIconSize.value
        
        assertTrue(smallToMediumRatio >= 1.1f, "Medium icons should be 1.1x+ larger than small")
        assertTrue(mediumToLargeRatio >= 1.1f, "Large icons should be 1.1x+ larger than medium")
        assertTrue(largeToTabletRatio >= 1.1f, "Tablet icons should be 1.1x+ larger than large")
    }
    
    @Test
    fun `grid columns are within valid range`() {
        val smallPhone = createSmallPhoneDimensions()
        val mediumPhone = createMediumPhoneDimensions()
        val largePhone = createLargePhoneDimensions()
        val tablet = createTabletDimensions()
        
        // Grid columns should be between 2 and 4
        assertTrue(smallPhone.gridColumns in 2..4, "Small phone grid columns should be 2-4")
        assertTrue(mediumPhone.gridColumns in 2..4, "Medium phone grid columns should be 2-4")
        assertTrue(largePhone.gridColumns in 2..4, "Large phone grid columns should be 2-4")
        assertTrue(tablet.gridColumns in 2..4, "Tablet grid columns should be 2-4")
    }
    
    @Test
    fun `tablet has more columns than phones`() {
        val phone = createMediumPhoneDimensions()
        val tablet = createTabletDimensions()
        
        assertTrue(tablet.gridColumns > phone.gridColumns, "Tablets should have more columns than phones")
    }
}
