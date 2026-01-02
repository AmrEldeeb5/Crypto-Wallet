package com.example.valguard.app.core.util

import com.example.valguard.theme.DarkCryptoColors
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceChangeColorPropertyTest {
    
    private val colors = DarkCryptoColors
    private val iterations = 100
    
    // Expected color values
    private val emerald400 = 0xFF34D399L  // profit color
    private val rose400 = 0xFFFB7185L     // loss color
    private val slate400 = 0xFF94A3B8L    // neutral color
    
    @Test
    fun `positive change values should return emerald-400 profit color`() {
        // Property: For any positive change value, color should be emerald-400
        repeat(iterations) {
            val positiveChange = Random.nextDouble(0.001, 1000.0)
            val colorHex = getPriceChangeColorHex(positiveChange)
            
            assertEquals(
                emerald400,
                colorHex,
                "Positive change $positiveChange should return emerald-400 (0xFF34D399)"
            )
        }
    }
    
    @Test
    fun `negative change values should return rose-400 loss color`() {
        // Property: For any negative change value, color should be rose-400
        repeat(iterations) {
            val negativeChange = Random.nextDouble(-1000.0, -0.001)
            val colorHex = getPriceChangeColorHex(negativeChange)
            
            assertEquals(
                rose400,
                colorHex,
                "Negative change $negativeChange should return rose-400 (0xFFFB7185)"
            )
        }
    }
    
    @Test
    fun `zero change should return slate-400 neutral color`() {
        // Property: Zero change should return neutral color
        val colorHex = getPriceChangeColorHex(0.0)
        
        assertEquals(
            slate400,
            colorHex,
            "Zero change should return slate-400 (0xFF94A3B8)"
        )
    }
    
    @Test
    fun `very small positive values should still return profit color`() {
        // Edge case: Very small positive values
        val smallPositives = listOf(0.0001, 0.00001, 0.000001, Double.MIN_VALUE)
        
        smallPositives.forEach { change ->
            val colorHex = getPriceChangeColorHex(change)
            assertEquals(
                emerald400,
                colorHex,
                "Small positive change $change should return emerald-400"
            )
        }
    }
    
    @Test
    fun `very small negative values should still return loss color`() {
        // Edge case: Very small negative values
        val smallNegatives = listOf(-0.0001, -0.00001, -0.000001, -Double.MIN_VALUE)
        
        smallNegatives.forEach { change ->
            val colorHex = getPriceChangeColorHex(change)
            assertEquals(
                rose400,
                colorHex,
                "Small negative change $change should return rose-400"
            )
        }
    }
    
    @Test
    fun `large positive values should return profit color`() {
        // Edge case: Large positive values
        val largePositives = listOf(100.0, 1000.0, 10000.0, 999999.99)
        
        largePositives.forEach { change ->
            val colorHex = getPriceChangeColorHex(change)
            assertEquals(
                emerald400,
                colorHex,
                "Large positive change $change should return emerald-400"
            )
        }
    }
    
    @Test
    fun `large negative values should return loss color`() {
        // Edge case: Large negative values
        val largeNegatives = listOf(-100.0, -1000.0, -10000.0, -999999.99)
        
        largeNegatives.forEach { change ->
            val colorHex = getPriceChangeColorHex(change)
            assertEquals(
                rose400,
                colorHex,
                "Large negative change $change should return rose-400"
            )
        }
    }
    
    @Test
    fun `getPriceChangeColor with CryptoColors returns correct colors`() {
        // Test the CryptoColors-based function
        val positiveColor = getPriceChangeColor(5.5, colors)
        val negativeColor = getPriceChangeColor(-3.2, colors)
        val neutralColor = getPriceChangeColor(0.0, colors)
        
        assertEquals(colors.profit, positiveColor, "Positive should return profit color")
        assertEquals(colors.loss, negativeColor, "Negative should return loss color")
        assertEquals(colors.neutral, neutralColor, "Zero should return neutral color")
    }
    
    @Test
    fun `getPriceChangeDirection returns correct direction for random values`() {
        // Property: Direction should match sign of change
        repeat(iterations) {
            val change = Random.nextDouble(-1000.0, 1000.0)
            val direction = getPriceChangeDirection(change)
            
            val expectedDirection = when {
                change > 0 -> PriceChangeDirection.POSITIVE
                change < 0 -> PriceChangeDirection.NEGATIVE
                else -> PriceChangeDirection.NEUTRAL
            }
            
            assertEquals(
                expectedDirection,
                direction,
                "Change $change should have direction $expectedDirection"
            )
        }
    }
    
    @Test
    fun `color mapping is consistent across multiple calls`() {
        // Property: Same input should always produce same output
        repeat(iterations) {
            val change = Random.nextDouble(-1000.0, 1000.0)
            val color1 = getPriceChangeColorHex(change)
            val color2 = getPriceChangeColorHex(change)
            
            assertEquals(
                color1,
                color2,
                "Same change value $change should always return same color"
            )
        }
    }
    
    @Test
    fun `boundary values around zero are handled correctly`() {
        // Test values very close to zero
        val nearZeroPositive = 0.0000000001
        val nearZeroNegative = -0.0000000001
        
        assertEquals(
            emerald400,
            getPriceChangeColorHex(nearZeroPositive),
            "Near-zero positive should return profit color"
        )
        
        assertEquals(
            rose400,
            getPriceChangeColorHex(nearZeroNegative),
            "Near-zero negative should return loss color"
        )
    }
    
    @Test
    fun `negative zero is treated as zero`() {
        // -0.0 should be treated as 0.0
        val negativeZero = -0.0
        val colorHex = getPriceChangeColorHex(negativeZero)
        
        assertEquals(
            slate400,
            colorHex,
            "Negative zero should return neutral color"
        )
    }
}
